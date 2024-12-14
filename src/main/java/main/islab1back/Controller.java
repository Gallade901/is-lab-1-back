package main.islab1back;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import main.islab1back.entity.SessionUser;
import main.islab1back.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/controller")
public class Controller {
    private EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private EntityTransaction transaction = em.getTransaction();

    @POST
    @Path("/addFlat")
    public void addFlat() {

    }
    @POST
    @Path("/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String registration(User user) {
        transaction.begin();
        if (!em.createQuery("SELECT u FROM User u WHERE u.login = :login")
                .setParameter("login", user.getLogin())
                .getResultList().isEmpty()) {
            return "Такой логин уже существует";
        }
        em.persist(user);
        transaction.commit();
        return "Регистрация прошла успешно";
    }
    @POST
    @Path("/authorization")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorization(User user) {
        try {
            transaction.begin();
            User authenticatedUser = (User) em.createQuery("SELECT u FROM User u WHERE u.login = :login and u.password = :password")
                    .setParameter("login", user.getLogin())
                    .setParameter("password", user.getPassword())
                    .getSingleResult();
            String sessionId = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(4);
            SessionUser session = new SessionUser(sessionId, authenticatedUser, expiresAt);
            em.persist(session);
            transaction.commit();
            NewCookie sessionCookie = new NewCookie("sessionId", sessionId, "/", null, null, 14400, false, true);
            return Response.ok("Авторизация успешна").cookie(sessionCookie).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("/checkAuthorization")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAuthorization(@CookieParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(false).build();
        }

        try {
            SessionUser session = em.createQuery(
                            "SELECT s FROM SessionUser s WHERE s.id = :sessionId AND s.expiresAt > :now", SessionUser.class)
                    .setParameter("sessionId", sessionId)
                    .setParameter("now", LocalDateTime.now())
                    .getSingleResult();

            // Если сессия найдена и не истекла
            return Response.ok(true).build();
        } catch (NoResultException e) {
            // Если сессия не найдена
            return Response.status(Response.Status.UNAUTHORIZED).entity(false).build();
        }
    }

}
