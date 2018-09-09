package models.socket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class NotificationEncoder implements Encoder.Text<Notification> {

	private static Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}

	@Override
	public String encode(Notification notification) throws EncodeException {
		return gson.toJson(notification);
	}

}
