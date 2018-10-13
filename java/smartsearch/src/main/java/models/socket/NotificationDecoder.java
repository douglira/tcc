package models.socket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class NotificationDecoder implements Decoder.Text<ArrayList<Notification>> {

	private static Gson gson = new Gson();

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public ArrayList<Notification> decode(String payload) throws DecodeException {
		Type listType = new TypeToken<ArrayList<Notification>>(){}.getType();
		return gson.fromJson(payload, listType);
	}

	@Override
	public boolean willDecode(String payload) {
		return (payload != null);
	}

	@Override
	public void destroy() {

	}
}
