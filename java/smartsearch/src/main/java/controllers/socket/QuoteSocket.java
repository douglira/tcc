package controllers.socket;

import dao.QuoteDAO;
import models.Quote;
import models.socket.QuoteDecoder;
import models.socket.QuoteEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/account/purchase_request/{purchaseRequestId}/quotes", encoders = QuoteEncoder.class, decoders = QuoteDecoder.class)
public class QuoteSocket {
    private Session session;
    private static Set<QuoteSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, Integer> purchaseRequests = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("purchaseRequestId") String purchaseRequestId) throws IOException {
        this.session = session;
        endpoints.add(this);
        purchaseRequests.put(session.getId(), Integer.parseInt(purchaseRequestId));
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuoteSocket ERROR: " + throwable.getMessage());
    }

    public static void sendUpdatedQuotes(Integer purchaseRequestId) {
        if (isInvalidPurchaseRequestId(purchaseRequestId)) {
            return;
        }

        ArrayList<Quote> quotes = new QuoteDAO(true).findByPurchaseRequest(purchaseRequestId);
        sendQuotes(purchaseRequestId, quotes);
    }

    public static void sendUpdatedQuotes(Integer purchaseRequestId, ArrayList<Quote> quotes) {
        if (isInvalidPurchaseRequestId(purchaseRequestId)) {
             return;
        }

        if (quotes == null || quotes.isEmpty()) {
            sendUpdatedQuotes(purchaseRequestId);
            return;
        }

        sendQuotes(purchaseRequestId, quotes);
    }

    private static void sendQuotes(Integer purchaseRequestId, ArrayList<Quote> quotes) {
        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                Integer prId = purchaseRequests.get(endpoint.session.getId());

                if (purchaseRequestId.equals(prId)) {
                    try {
                        endpoint.session.getBasicRemote().sendObject(quotes);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static boolean isInvalidPurchaseRequestId(Integer purchaseRequestId) {
        return purchaseRequestId == null || purchaseRequestId == 0;
    }
}
