package controllers.socket;

import dao.FileDAO;
import dao.PRProductListDAO;
import dao.PurchaseRequestDAO;
import enums.PRStage;
import models.*;
import models.socket.PurchaseRequestDecoder;
import models.socket.PurchaseRequestEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/account/purchase_request/{username}", encoders = PurchaseRequestEncoder.class, decoders = PurchaseRequestDecoder.class)
public class PurchaseRequestSocket {

    private Session session;
    private static Set<PurchaseRequestSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        this.session = session;
        endpoints.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, PurchaseRequest purchaseRequest) throws IOException {
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("PurchaseRequestSocket error on connect: " + throwable.getMessage());
    }

    public static void sendUpdatedPRCreation(User user, PurchaseRequest purchaseRequest, String baseUrl) {
        if (purchaseRequest == null) {
            purchaseRequest = PurchaseRequestSocket.getPurchaseRequest(user, baseUrl);
        }

        PurchaseRequest prPayload = purchaseRequest;

        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                String username = PurchaseRequestSocket.users.get(endpoint.session.getId());

                if (username.equals(user.getUsername())) {
                    try {
                        endpoint.session.getBasicRemote()
                                .sendObject(prPayload);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static PurchaseRequest getPurchaseRequest(User user, String baseUrl) {
        Buyer buyer = new Buyer();
        buyer.setId(user.getPerson().getId());

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setBuyer(buyer);
        purchaseRequest.setStage(PRStage.CREATION);

        ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
        if (prs != null && !prs.isEmpty()) {
            purchaseRequest = prs.get(0);

            ArrayList<ProductList> products = new PRProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());
            products.forEach(productList -> {
                synchronized (productList) {
                    ProductItem productItem = (ProductItem) productList.getProduct();
                    productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                    productItem.setDefaultThumbnail(baseUrl);
                }
            });

            products.sort(ProductList::compareTo);
            purchaseRequest.setListProducts(products);
            purchaseRequest.calculateAmount();
        }

        return purchaseRequest;
    }
}