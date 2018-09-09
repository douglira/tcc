package controllers.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import models.socket.Notification;
import models.socket.NotificationDecoder;
import models.socket.NotificationEncoder;

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
		System.out.println("User connected: " + session.getId() + " - " + username);
	}

	@OnMessage
	public void onMessage(Session session, Notification notification) throws IOException {
		notification.setFrom(users.get(session.getId()));
		System.out.println("Socket onMessage[Notification]: " + notification);
		sendNotification(notification);
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		notifyEndpoints.remove(this);
		System.out.println("User disconnected: " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
	}
	
	public static void sendNotification(Notification notification) {		
		
		notifyEndpoints.forEach(endpoint -> {
			synchronized (endpoint) {
				String username = NotificationSocket.users.get(endpoint.session.getId());

				if (username.equals(notification.getTo())) {
					try {
						System.out.println("Sending notification from " + notification.getFrom() + " to " + notification.getTo());
						endpoint.session.getBasicRemote().
						sendObject(notification);
					} catch (IOException | EncodeException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}