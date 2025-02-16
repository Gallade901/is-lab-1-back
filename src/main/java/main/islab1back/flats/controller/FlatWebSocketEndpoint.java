package main.islab1back.flats.controller;


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
@ServerEndpoint("/flat")
public class FlatWebSocketEndpoint {
    private static final Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        FlatController controller = new FlatController();
        Response response = controller.getFlats();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
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
        FlatController controller = new FlatController();
        Response response = controller.getFlats();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

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