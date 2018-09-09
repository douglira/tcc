package models.socket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class NotificationDecoder implements Decoder.Text<Notification> {

	private static Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public Notification decode(String payload) throws DecodeException {
		return gson.fromJson(payload, Notification.class);
	}

	@Override
	public boolean willDecode(String payload) {
		return (payload != null);
	}

	@Override
	public void destroy() {

	}
}
