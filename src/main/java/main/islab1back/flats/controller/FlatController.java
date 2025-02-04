package main.islab1back.flats.controller;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.websocket.Session;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.flats.controller.FlatWebSocketEndpoint;
import main.islab1back.flats.dto.FlatDtoRequest;
import main.islab1back.flats.dto.FlatDtoRequestEdit;
import main.islab1back.flats.dto.FlatDtoResponse;
import main.islab1back.flats.model.Flat;
import main.islab1back.house.model.House;
import main.islab1back.user.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/flat")
public class FlatController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();
    FlatWebSocketEndpoint flatWebSocket = new FlatWebSocketEndpoint();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFlat(@Valid FlatDtoRequest flatDtoRequest) {
        try {
            transaction.begin();
            String login = flatDtoRequest.getLogin();
            User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
            Coordinates coordinates;
            House house;
            if (flatDtoRequest.getCoordinatesId().equals(0)) {
                coordinates = new Coordinates(flatDtoRequest.getCoordinateX(), flatDtoRequest.getCoordinateY(), user);
                em.persist(coordinates);
            } else {
                coordinates = em.find(Coordinates.class, flatDtoRequest.getCoordinatesId());
            }
            if (flatDtoRequest.getHouseId().equals(0)) {
                int houseYear = flatDtoRequest.getHouseYear();
                int houseNumberOfFloors = flatDtoRequest.getHouseNumberOfFloors();
                if (houseYear <= 0 || houseNumberOfFloors <= 0 || houseYear > 681 || houseNumberOfFloors > 80) {
                    return Response.ok("Невалидное количество этажей или год дома").build();
                }
                house = new House(flatDtoRequest.getHouseName(), flatDtoRequest.getHouseYear(), flatDtoRequest.getHouseNumberOfFloors(), user);
                em.persist(house);
            } else if (flatDtoRequest.getHouseId().equals(-1)) {
                house = null;
            }
            else {
                house = em.find(House.class, flatDtoRequest.getHouseId());
            }
            Flat flat = new Flat(flatDtoRequest.getName(), coordinates, house, flatDtoRequest.getArea(), flatDtoRequest.getPrice(), flatDtoRequest.getBalcony(),
                    flatDtoRequest.getTimeToMetroOnFoot(), flatDtoRequest.getNumberOfRooms(), flatDtoRequest.getFurnish(), flatDtoRequest.getView(), flatDtoRequest.getTransport(), user);
            flat.setCreationDate(LocalDate.now());
            em.persist(flat);
            transaction.commit();
            flatWebSocket.onMessage("");
            return Response.ok("Квартира добавлена").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при добавлении квартиры").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlats() {
        try {
            transaction.begin();
            List<Flat> flats = em.createQuery("SELECT f FROM Flat f", Flat.class)
                    .getResultList();
            List<FlatDtoResponse> flatsResponse = new ArrayList<>();
            for (Flat f : flats) {
                Coordinates coordinates = f.getCoordinates();
                House house;
                if (f.getHouse() == null) {
                    house = new House("",null,null,null);
                } else {
                    house = f.getHouse();
                }
                FlatDtoResponse flatDtoResponse = new FlatDtoResponse(f.getId(), f.getName(), coordinates.getX(), coordinates.getY(), house.getName(), house.getYear(), house.getNumberOfFloors(),
                         f.getNumberOfRooms(), f.getArea(),f.getCreationDate(), f.getPrice(), f.getBalcony(), f.getTimeToMetroOnFoot(), f.getFurnish(), f.getView(), f.getTransport(), f.getUser().getLogin());
                flatsResponse.add(flatDtoResponse);
            }
            transaction.commit();
            return Response.ok(flatsResponse).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при получении квартир").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteFlat(@PathParam("id") Integer id) {
        try {
            transaction.begin();
            Flat flat = em.find(Flat.class, id);
            if (flat != null) {
                em.remove(flat);
            }
            transaction.commit();
            flatWebSocket.onMessage("");
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
    public Response updateFlat(@Valid FlatDtoRequestEdit flatDtoRequestEdit) {
        transaction.begin();
        String login = flatDtoRequestEdit.getOwner();
        User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        Flat existingFlat = em.find(Flat.class, flatDtoRequestEdit.getId());

        if (existingFlat == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Flat not found").build();
        }

        existingFlat.setName(flatDtoRequestEdit.getName());
        existingFlat.setArea(flatDtoRequestEdit.getArea());
        existingFlat.setPrice(flatDtoRequestEdit.getPrice());
        existingFlat.setBalcony(flatDtoRequestEdit.getBalcony());
        existingFlat.setTimeToMetroOnFoot(flatDtoRequestEdit.getTimeToMetroOnFoot());
        existingFlat.setFurnish(flatDtoRequestEdit.getFurnish());
        existingFlat.setView(flatDtoRequestEdit.getView());
        existingFlat.setTransport(flatDtoRequestEdit.getTransport());

        Coordinates coordinates;
        House house;
        if (flatDtoRequestEdit.getCoordinatesId().equals(0)) {
            coordinates = new Coordinates(flatDtoRequestEdit.getCoordinateX(), flatDtoRequestEdit.getCoordinateY(), user);
            em.persist(coordinates);
        } else {
            coordinates = em.find(Coordinates.class, flatDtoRequestEdit.getCoordinatesId());
        }
        if (flatDtoRequestEdit.getHouseId().equals(0)) {
            int houseYear = flatDtoRequestEdit.getHouseYear();
            int houseNumberOfFloors = flatDtoRequestEdit.getHouseNumberOfFloors();
            if (houseYear <= 0 || houseNumberOfFloors <= 0 || houseYear > 681 || houseNumberOfFloors > 80) {
                return Response.ok("Невалидное количество этажей или год дома").build();
            }
            house = new House(flatDtoRequestEdit.getHouseName(), flatDtoRequestEdit.getHouseYear(), flatDtoRequestEdit.getHouseNumberOfFloors(), user);
            em.persist(house);
        } else if (flatDtoRequestEdit.getHouseId().equals(-1)) {
            house = null;
        }
        else {
            house = em.find(House.class, flatDtoRequestEdit.getHouseId());
        }

        existingFlat.setHouse(house);
        existingFlat.setCoordinates(coordinates);

        em.merge(existingFlat);
        transaction.commit();
        flatWebSocket.onMessage("");
        return Response.ok("Квартира изменена").build();
    }

    @GET
    @Path("/getId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlatId(@QueryParam("id") Integer id) {
        Flat f = em.find(Flat.class, id);
        Coordinates coordinates = f.getCoordinates();
        House house;
        if (f.getHouse() == null) {
            house = new House("",null,null,null);
        } else {
            house = f.getHouse();
        }
        Map<String, Object> flatResponse = new HashMap<>();
        FlatDtoRequest flatDtoRequest = new FlatDtoRequest();
        FlatDtoResponse flatDtoResponse = new FlatDtoResponse(f.getId(), f.getName(), coordinates.getX(), coordinates.getY(), house.getName(), house.getYear(), house.getNumberOfFloors(),
                f.getNumberOfRooms(), f.getArea(), f.getCreationDate(), f.getPrice(), f.getBalcony(), f.getTimeToMetroOnFoot(), f.getFurnish(), f.getView(), f.getTransport(), f.getUser().getLogin());
        return Response.ok(flatDtoResponse).build();
    }
}
