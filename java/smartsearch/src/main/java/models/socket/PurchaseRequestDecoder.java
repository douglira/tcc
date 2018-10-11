package models.socket;

import com.google.gson.Gson;
import models.PurchaseRequest;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class PurchaseRequestDecoder implements Decoder.Text<PurchaseRequest> {
    
    private static Gson gson = new Gson();
    
    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public PurchaseRequest decode(String payload) throws DecodeException {
        return gson.fromJson(payload, PurchaseRequest.class);
    }

    @Override
    public boolean willDecode(String payload) {
        return (payload != null);
    }

    @Override
    public void destroy() {

    }
}
