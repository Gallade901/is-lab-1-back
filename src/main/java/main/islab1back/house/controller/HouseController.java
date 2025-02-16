package main.islab1back.house.controller;

import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.flats.controller.FlatWebSocketEndpoint;
import main.islab1back.flats.model.Flat;
import main.islab1back.house.dto.HouseDtoRequest;
import main.islab1back.house.dto.HouseDtoRequestEdit;
import main.islab1back.house.dto.HouseDtoResponse;
import main.islab1back.house.model.House;
import main.islab1back.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/house")
public class HouseController {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myDb");
    private final EntityManager em = emf.createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();
    HouseWebSocketEndpoint houseWebSocket = new HouseWebSocketEndpoint();
    FlatWebSocketEndpoint flatWebSocket = new FlatWebSocketEndpoint();

    @PreDestroy
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHouse(@Valid HouseDtoRequest houseDto) {
        try {
            transaction.begin();
            String login = houseDto.getLogin();
            User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
            List<Flat> flatList = new ArrayList<>();
            House house = new House(houseDto.getName(), houseDto.getYear(), houseDto.getNumberOfFloors(), user, flatList);
            em.persist(house);
            transaction.commit();
            houseWebSocket.onMessage("");
            return Response.ok("Дом успешно добавлен").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при добавлении дома").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouses() {
        try {
            transaction.begin();
            List<House> houses = em.createQuery("SELECT h FROM House h", House.class)
                    .getResultList();
            List<HouseDtoResponse> housesResponse = new ArrayList<>();
            for (House house : houses) {
                HouseDtoResponse houseDtoResponse = new HouseDtoResponse(house.getId(), house.getName(), house.getYear(), house.getNumberOfFloors(), house.getUser().getLogin());
                housesResponse.add(houseDtoResponse);
            }
            transaction.commit();
            return Response.ok(housesResponse).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при получении домов").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteHouse(@PathParam("id") Integer id) {
        try {
            transaction.begin();

            House house = em.find(House.class, id);
            if (house != null) {
                em.remove(house);
            }
            transaction.commit();
            houseWebSocket.onMessage("");
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
    public Response updateFlat(@Valid HouseDtoRequestEdit houseDtoRequest) {
        transaction.begin();
        String login = houseDtoRequest.getOwner();
        User user = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        House existingHouse = em.find(House.class, houseDtoRequest.getId());

        if (existingHouse == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Flat not found").build();
        }

        existingHouse.setName(houseDtoRequest.getName());
        existingHouse.setYear(houseDtoRequest.getYear());
        existingHouse.setNumberOfFloors(houseDtoRequest.getNumberOfFloors());

        em.merge(existingHouse);
        transaction.commit();
        houseWebSocket.onMessage("");
        return Response.ok("Дом изменен").build();
    }

    @GET
    @Path("/getId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlatId(@QueryParam("id") Integer id) {
        House house = em.find(House.class, id);
        HouseDtoResponse houseDtoResponse = new HouseDtoResponse(house.getId(), house.getName(), house.getYear(), house.getNumberOfFloors(), house.getUser().getLogin());
        return Response.ok(houseDtoResponse).build();
    }
}
