package controllers.socket;

import dao.QuoteDAO;
import models.PurchaseRequest;
import models.Quote;
import models.User;
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

@ServerEndpoint(value = "/account/seller/{sellerUsername}/purchase_request/{purchaseRequestId}/quotes", encoders = QuoteEncoder.class, decoders = QuoteDecoder.class)
public class QuoteSocket {
    private Session session;
    private static Set<QuoteSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, Integer> purchaseRequests = new HashMap<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sellerUsername") String username, @PathParam("purchaseRequestId") String purchaseRequestId) throws IOException {
        this.session = session;
        endpoints.add(this);
        purchaseRequests.put(session.getId(), Integer.parseInt(purchaseRequestId));
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(ArrayList<Quote> quotes, Session session) {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuoteSocket ERROR: " + throwable.getMessage());
    }

    public static void sendUpdatedQuotes(PurchaseRequest purchaseRequest, ArrayList<Quote> quotes) {
        if (validatePurchaseRequest(purchaseRequest)) return;

        if (validateQuotes(quotes)) return;

        sendQuotes(purchaseRequest, quotes);
    }

    private static void sendQuotes(PurchaseRequest purchaseRequest, ArrayList<Quote> quotes) {
        if (!purchaseRequest.getQuotesVisibility()) return;

        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                Integer prId = purchaseRequests.get(endpoint.session.getId());

                if (prId.equals(purchaseRequest.getId())) {
                    try {
                        endpoint.session.getBasicRemote().sendObject(quotes);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void sendUpdatedRestrictQuotes(User sellerUser, PurchaseRequest purchaseRequest, ArrayList<Quote> quotes) throws Throwable {
        if (validatePurchaseRequest(purchaseRequest)) return;
        if (validateQuotes(quotes)) return;
        if (purchaseRequest.getQuotesVisibility()) throw new Throwable("Only restrict quotes visibility");
        if (sellerUser.getUsername() == null) return;

        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                String username = users.get(endpoint.session.getId());

                if (username.equals(sellerUser.getUsername())) {
                    try {
                        endpoint.session.getBasicRemote().sendObject(quotes);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static boolean validatePurchaseRequest(PurchaseRequest purchaseRequest) {
        return purchaseRequest == null || purchaseRequest.getId() == null || purchaseRequest.getId() == 0;
    }

    private static boolean validateQuotes(ArrayList<Quote> quotes) {
        return quotes == null || quotes.isEmpty();
    }
}
