package models.socket;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class PRCreationDecoder implements Decoder.Text<PRCreation> {
    
    private static Gson gson = new Gson();
    
    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public PRCreation decode(String payload) throws DecodeException {
        return gson.fromJson(payload, PRCreation.class);
    }

    @Override
    public boolean willDecode(String payload) {
        return (payload != null);
    }

    @Override
    public void destroy() {

    }
}
