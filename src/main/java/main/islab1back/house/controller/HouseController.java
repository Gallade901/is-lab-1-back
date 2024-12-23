package main.islab1back.house.controller;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.house.dto.HouseDtoRequest;
import main.islab1back.house.dto.HouseDtoResponse;
import main.islab1back.house.model.House;
import main.islab1back.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/house")
public class HouseController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();

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
            House house = new House(houseDto.getName(), houseDto.getYear(), houseDto.getNumberOfFloors(), user);
            em.persist(house);
            transaction.commit();
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
    public Response subClass() {
        try {
            transaction.begin();
            List<House> houses = em.createQuery("SELECT h FROM House h", House.class)
                    .getResultList();
            List<HouseDtoResponse> housesResponse = new ArrayList<>();
            for (House house : houses) {
                HouseDtoResponse houseDtoResponse = new HouseDtoResponse(house.getId(), house.getName(), house.getYear(), house.getNumberOfFloors());
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
}
