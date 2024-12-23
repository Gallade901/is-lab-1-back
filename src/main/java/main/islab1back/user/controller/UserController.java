package main.islab1back.user.controller;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import main.islab1back.dto.AnswerApplication;
import main.islab1back.user.model.ApplicationAdmin;
import main.islab1back.user.model.SessionUser;
import main.islab1back.user.model.User;
import main.islab1back.utils.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Singleton
@Path("/user")
public class UserController {
    private final EntityManager em = Persistence.createEntityManagerFactory("myDb").createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();

    @Schedule(hour = "*/2", persistent = false)
    public void cleanExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<SessionUser> noActiveSessions = em.createQuery("SELECT s FROM SessionUser s WHERE s.expiresAt <= :currentTime", SessionUser.class)
                .setParameter("currentTime", now)
                .getResultList();
        for (SessionUser session : noActiveSessions) {
            em.remove(session);
        }
    }
    @POST
    @Path("/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String registration(User user) {
        transaction.begin();
        if (!em.createQuery("SELECT u FROM User u WHERE u.login = :login OR EXISTS (SELECT a FROM ApplicationAdmin a WHERE a.login = :login)")
                .setParameter("login", user.getLogin())
                .getResultList().isEmpty()) {
            transaction.commit();
            return "Такой логин уже существует";
        }
        if (user.getRole().equals("ADMIN")) {
            if (!em.createQuery("SELECT u FROM User u WHERE u.role = :role")
                    .setParameter("role", "ADMIN")
                    .getResultList().isEmpty()) {
                ApplicationAdmin applicationAdmin = new ApplicationAdmin();
                applicationAdmin.setLogin(user.getLogin());
                applicationAdmin.setPassword(PasswordHasher.hashPassword(user.getPassword()));
                applicationAdmin.setRole(user.getRole());
                em.persist(applicationAdmin);
                transaction.commit();
                return "Заявка создана";
            }
        }
        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
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
                    .setParameter("password", PasswordHasher.hashPassword(user.getPassword()))
                    .getSingleResult();
            String sessionId = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(4);
            SessionUser session = new SessionUser(sessionId, authenticatedUser, expiresAt);
            em.persist(session);
            transaction.commit();
            NewCookie sessionCookie = new NewCookie("sessionId", sessionId, "/", null, null, 14400, false, true);
            return Response.ok("Авторизация успешна").cookie(sessionCookie).build();
        } catch (NoResultException e) {
            transaction.commit();
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
            return Response.ok(session.getUser().getLogin()).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(false).build();
        }
    }

    @POST
    @Path("/logout")
    public Response logOut(@CookieParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Сессия не найдена").build();
        }
        try {
            transaction.begin();
            int deletedCount = em.createQuery("DELETE FROM SessionUser s WHERE s.id = :sessionId")
                    .setParameter("sessionId", sessionId)
                    .executeUpdate();
            transaction.commit();

            if (deletedCount > 0) {
                return Response.ok("Вы вышли из системы").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Сессия не найдена").build();
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при выходе из системы").build();
        }
    }

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response applications() {
        try {
            transaction.begin();
            List<ApplicationAdmin> applications = em.createQuery("SELECT a FROM ApplicationAdmin a", ApplicationAdmin.class)
                    .getResultList();
            transaction.commit();
            return Response.ok(applications).build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при получении заявок").build();
        }
    }

    @POST
    @Path("/answerApplication")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response approveApplication(AnswerApplication request) {
        try {
            transaction.begin();
            boolean flag = request.isFlag();
            ApplicationAdmin app = em.createQuery(
                            "SELECT a FROM ApplicationAdmin a WHERE a.login = :login", ApplicationAdmin.class)
                    .setParameter("login", request.getLogin())
                    .getSingleResult();
            User user = new User();
            user.setLogin(app.getLogin());
            user.setPassword(app.getPassword());
            user.setRole(app.getRole());
            if (flag) {
                em.persist(user);
            }
            em.remove(app);
            transaction.commit();
            return Response.ok("Заявка одобрена и пользователь добавлен").build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Заявка не найдена").build();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ошибка при одобрении заявки").build();
        }
    }

}
