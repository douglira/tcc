package controllers.socket;

import models.socket.Notification;
import models.socket.PRCreation;
import models.socket.PRCreationDecoder;
import models.socket.PRCreationEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/purchase_request/creation/{username}", encoders = PRCreationEncoder.class, decoders = PRCreationDecoder.class)
public class PRCreationSocket {

    private Session session;
    private static Set<PRCreationSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        this.session = session;
        endpoints.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, PRCreation prCreation) throws IOException {
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    }

    public static void sendPurchaseRequestUpdated(PRCreation prCreation) {

        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                String username = PRCreationSocket.users.get(endpoint.session.getId());

                if (username.equals(prCreation.getTo().getUsername())) {
                    try {
                        endpoint.session.getBasicRemote().
                                sendObject(prCreation);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}