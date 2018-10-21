package controllers.socket;

import dao.NotificationDAO;
import dao.PersonDAO;
import dao.UserDAO;
import enums.NotificationStatus;
import models.Person;
import models.User;
import models.socket.Notification;
import models.socket.NotificationDecoder;
import models.socket.NotificationEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/notify/{username}", encoders = NotificationEncoder.class, decoders = NotificationDecoder.class)
public class NotificationSocket {

    private Session session;
    private static Set<NotificationSocket> notifyEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        this.session = session;
        notifyEndpoints.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, ArrayList<Notification> notifications) throws IOException {
        if (notifications != null && !notifications.isEmpty()) {
            Notification notification = notifications.get(0);
            notification.setStatus(NotificationStatus.VIEWED);
            new NotificationDAO(true).updateStatus(notification);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        notifyEndpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("NotificationSocket ERROR: " + throwable.getMessage());
    }

    public static void pushLastNotifications(User user) {

        ArrayList<Notification> notifications = NotificationSocket.fetchLastNotifications(user);

        if (!notifications.isEmpty()) {
            notifyEndpoints.forEach(endpoint -> {
                synchronized (endpoint) {
                    String username = NotificationSocket.users.get(endpoint.session.getId());

                    if (username.equals(user.getUsername())) {
                        try {
                            endpoint.session.getBasicRemote()
                                    .sendObject(notifications);
                        } catch (IOException | EncodeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private static ArrayList<Notification> fetchLastNotifications(User user) {
        if (user.getId() == 0) {
            user = new UserDAO(true).findByUsername(user);
        }

        ArrayList<Notification> notifications = new NotificationDAO(true).findLastOnes(user.getId());

        notifications.forEach(notification -> {
            synchronized (notification) {
                if (notification.getFrom() != null) {
                    User userFrom = new UserDAO(true).findById(notification.getFrom());
                    Person person = new Person();
                    person.setUser(userFrom);
                    person = new PersonDAO(true).findByUser(person);
                    person.setUser(null);
                    userFrom.setPerson(person);
                    notification.setFrom(userFrom);
                }
            }
        });

        return notifications;
    }
}