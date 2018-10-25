package controllers.socket;

import models.PurchaseRequest;
import models.socket.PurchaseRequestDecoder;
import models.socket.PurchaseRequestEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/account/purchase_request/{purchaseRequestId}/quotes", encoders = PurchaseRequestEncoder.class, decoders = PurchaseRequestDecoder.class)
public class QuoteNotifierSocket {
    private Session session;
    private static Set<QuoteNotifierSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, Integer> purchaseRequests = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("purchaseRequestId") String purchaseRequestId) throws IOException {
        this.session = session;
        endpoints.add(this);
        purchaseRequests.put(session.getId(), Integer.parseInt(purchaseRequestId));
    }

    @OnMessage
    public void onMessage(PurchaseRequest purchaseRequest, Session session) {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        purchaseRequests.remove(this.session.getId());
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuoteNotifierSocket ERROR: " + throwable.getMessage());
        purchaseRequests.remove(this.session.getId());
        endpoints.remove(this);
    }

    public static void notifyUpdatedQuotes(PurchaseRequest purchaseRequest) {
        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                Integer prId = purchaseRequests.get(endpoint.session.getId());

                if (prId.equals(purchaseRequest.getId())) {

                    try {
                        endpoint.session.getBasicRemote().sendObject(purchaseRequest);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
