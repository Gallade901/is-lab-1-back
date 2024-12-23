package main.islab1back.flats.controller;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import main.islab1back.coordinates.model.Coordinates;
import main.islab1back.flats.dto.FlatDtoRequest;
import main.islab1back.flats.model.Flat;
import main.islab1back.house.model.House;
import main.islab1back.user.model.User;

import java.time.LocalDate;

@Singleton
@Path("/flat")
public class FlatController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();

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
                house = new House(flatDtoRequest.getHouseName(), flatDtoRequest.getHouseYear(), flatDtoRequest.getHouseNumberOfFloors(), user);
                em.persist(house);
            } else {
                house = em.find(House.class, flatDtoRequest.getHouseId());
                System.out.println(house);
            }
            Flat flat = new Flat(flatDtoRequest.getName(), coordinates, house, flatDtoRequest.getArea(), flatDtoRequest.getPrice(), flatDtoRequest.getBalcony(),
                    flatDtoRequest.getTimeToMetroOnFoot(), flatDtoRequest.getHouseNumberOfFloors(), flatDtoRequest.getFurnish(), flatDtoRequest.getView(), flatDtoRequest.getTransport(), user);
            flat.setCreationDate(LocalDate.now());
            em.persist(flat);
            transaction.commit();
            return Response.ok("Квартира добавлена").build();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при добавлении квартиры").build();
        }
    }
}
