package controllers.socket;

/*import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.mail.Message;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/notify/{username}")
public class HeaderSocket {

	private Session session;
	private static Set<HeaderSocket> notifyEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
		this.session = session;
		notifyEndpoints.add(this);
		users.put(session.getId(), username);
		System.out.println("Session -> " + session.getId());
	}

	@OnMessage
	public void onMessage(Session session, Message message) throws IOException {
		// Handle new messages
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		// WebSocket connection closes
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
	}
}
*/