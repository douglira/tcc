package models.socket;

import com.google.gson.Gson;
import models.Quote;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.ArrayList;

public class QuoteEncoder implements Encoder.Text<ArrayList<Quote>> {
    private static Gson gson = new Gson();

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public String encode(ArrayList<Quote> quotes) throws EncodeException {
        return gson.toJson(quotes);
    }

    @Override
    public void destroy() {

    }
}
