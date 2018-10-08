package models.socket;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class PRCreationEncoder implements Encoder.Text<PRCreation> {
    private static Gson gson = new Gson();

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(PRCreation prCreation) throws EncodeException {
        return gson.toJson(prCreation);
    }
}
