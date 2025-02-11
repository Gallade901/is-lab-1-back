package main.islab1back.utils;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import main.islab1back.user.controller.UserController;

import java.io.IOException;
import java.util.Map;

@Provider
@PreMatching
public class AuthorizationFilter implements ContainerRequestFilter {

    @Inject
    private UserController userController;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String currentPath =  requestContext.getUriInfo().getPath();

        // Пропускаем запросы к регистрации и корневому пути
        if ("/user/registration".equals(currentPath) || "/user/authorization".equals(currentPath)) {
            return;
        }

        Map<String, Cookie> cookies = requestContext.getCookies();
        Cookie sessionIdCookie = cookies.get("sessionId");
        String sessionId = sessionIdCookie != null ? sessionIdCookie.getValue() : null;

        if (sessionId == null || sessionId.trim().isEmpty()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(false)
                            .build()
            );
            return;
        }

        Response response = userController.checkAuthorization(sessionId);
        if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            requestContext.abortWith(response);
        }
    }
}