package main.islab1back.house.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
@ServerEndpoint("/house")
public class HouseWebSocketEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        HouseController controller = new HouseController();
        Response response = controller.getHouses();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(response.getEntity());
            session.getBasicRemote().sendText(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Отключился пользователь: " + session.toString());
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        sendMessageToAll();
    }

    private void sendMessageToAll() {
        HouseController controller = new HouseController();
        Response response = controller.getHouses();
        ObjectMapper mapper = new ObjectMapper();

        sessions.forEach(s -> {
            try {
                String jsonString = mapper.writeValueAsString(response.getEntity());
                s.getBasicRemote().sendText(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}