package models.socket;

import com.google.gson.Gson;
import models.PurchaseRequest;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class PurchaseRequestEncoder implements Encoder.Text<PurchaseRequest> {
    private static Gson gson = new Gson();

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(PurchaseRequest payload) throws EncodeException {
        return gson.toJson(payload);
    }
}
