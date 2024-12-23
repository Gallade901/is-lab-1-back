package main.islab1back.coordinates.controller;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.coordinates.dto.CoordinatesDtoRequest;
import main.islab1back.coordinates.dto.CoordinatesDtoResponse;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.user.model.User;

import java.util.ArrayList;
import java.util.List;


@Singleton
@Path("/coordinates")
public class CoordinatesController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();

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
            Coordinates coordinates = new Coordinates(coordinatesDto.getX(),coordinatesDto.getY(), user);
            em.persist(coordinates);
            transaction.commit();
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
                CoordinatesDtoResponse coordinatesDtoResponse = new CoordinatesDtoResponse(c.getId(), c.getX(), c.getY());
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
}
