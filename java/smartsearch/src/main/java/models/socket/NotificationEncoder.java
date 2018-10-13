package models.socket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import java.util.ArrayList;

public class NotificationEncoder implements Encoder.Text<ArrayList<Notification>> {

	private static Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}

	@Override
	public String encode(ArrayList<Notification> notifications) throws EncodeException {
		return gson.toJson(notifications);
	}

}
