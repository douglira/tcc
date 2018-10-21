package models.socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Quote;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuoteDecoder implements Decoder.Text<ArrayList<Quote>>{
    private static Gson gson = new Gson();

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public ArrayList<Quote> decode(String payload) throws DecodeException {
        Type listType = new TypeToken<ArrayList<Quote>>(){}.getType();
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
