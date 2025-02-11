package main.islab1back.coordinates.controller;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.coordinates.dto.CoordinateDtoRequestEdit;
import main.islab1back.coordinates.dto.CoordinatesDtoRequest;
import main.islab1back.coordinates.dto.CoordinatesDtoResponse;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.flats.controller.FlatWebSocketEndpoint;
import main.islab1back.flats.model.Flat;
import main.islab1back.user.model.User;

import java.util.ArrayList;
import java.util.List;


@Singleton
@Path("/coordinates")
public class CoordinatesController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();
    CoordinateWebSocketEndpoint coordinateWebSocket = new CoordinateWebSocketEndpoint();
    FlatWebSocketEndpoint flatWebSocketEndpoint = new FlatWebSocketEndpoint();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCoordinates(@Valid CoordinatesDtoRequest coordinatesDto) {
        try {
            transaction.begin();
            String login = coordinatesDto.getLogin();
            User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
            List<Flat> flatList = new ArrayList<>();
            Coordinates coordinates = new Coordinates(coordinatesDto.getX(),coordinatesDto.getY(), user, flatList);
            em.persist(coordinates);
            transaction.commit();
            coordinateWebSocket.onMessage("");
            return Response.ok("Координаты успешно добавлены").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при добавлении координат").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCoordinates() {
        try {
            transaction.begin();
            List<Coordinates> coordinates = em.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
                    .getResultList();
            List<CoordinatesDtoResponse> coordinatesResponse = new ArrayList<>();
            for (Coordinates c : coordinates) {
                CoordinatesDtoResponse coordinatesDtoResponse = new CoordinatesDtoResponse(c.getId(), c.getX(), c.getY(), c.getUser().getLogin());
                coordinatesResponse.add(coordinatesDtoResponse);
            }
            transaction.commit();
            return Response.ok(coordinatesResponse).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при получении координат").build();
        }
    }
    @DELETE
    @Path("/{id}")
    public Response deleteHouse(@PathParam("id") Integer id) {
        try {
            transaction.begin();

            Coordinates coordinate = em.find(Coordinates.class, id);
            if (coordinate != null) {
                em.remove(coordinate);
            }
            transaction.commit();
            coordinateWebSocket.onMessage("");
            flatWebSocketEndpoint.onMessage("");
            return Response.ok().build();

        }  catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFlat(@Valid CoordinateDtoRequestEdit coordinateDtoRequest) {
        transaction.begin();
        String login = coordinateDtoRequest.getOwner();
        User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        Coordinates existingCoordanate = em.find(Coordinates.class, coordinateDtoRequest.getId());

        if (existingCoordanate == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Flat not found").build();
        }

        existingCoordanate.setX(coordinateDtoRequest.getX());
        existingCoordanate.setY(coordinateDtoRequest.getY());


        em.merge(existingCoordanate);
        transaction.commit();
        coordinateWebSocket.onMessage("");
        return Response.ok("Координаты изменены").build();
    }

    @GET
    @Path("/getId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlatId(@QueryParam("id") Integer id) {
        Coordinates coordinate = em.find(Coordinates.class, id);
        CoordinatesDtoResponse coordinateDtoResponse = new CoordinatesDtoResponse(coordinate.getId(), coordinate.getX(), coordinate.getY(), coordinate.getUser().getLogin());
        return Response.ok(coordinateDtoResponse).build();
    }
}
