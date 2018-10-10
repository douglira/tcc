package controllers.socket;

import dao.FileDAO;
import dao.ProductListDAO;
import dao.PurchaseRequestDAO;
import enums.PRStage;
import libs.Helper;
import models.Buyer;
import models.ProductItem;
import models.ProductList;
import models.PurchaseRequest;
import models.socket.PRCreation;
import models.socket.PRCreationDecoder;
import models.socket.PRCreationEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/purchase_request/creation/{username}", encoders = PRCreationEncoder.class, decoders = PRCreationDecoder.class)
public class PRCreationSocket {

    private Session session;
    private static Set<PRCreationSocket> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        this.session = session;
        endpoints.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, PRCreation prCreation) throws IOException {
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    }

    public static void sendPurchaseRequestUpdated(PRCreation prCreation, String baseUrl) {
        if (prCreation.getPurchaseRequest() == null) {
            Buyer buyer = new Buyer();
            buyer.setId(prCreation.getTo().getPerson().getId());

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setBuyer(buyer);
            purchaseRequest.setStage(PRStage.CREATION);

            ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
            if (prs != null && !prs.isEmpty()) {
                purchaseRequest = prs.get(0);

                ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                products.forEach(productList -> {
                    synchronized (productList) {
                        ProductItem productItem = (ProductItem) productList.getProduct();
                        productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                        productItem.setDefaultThumbnail(baseUrl);
                    }
                });

                purchaseRequest.setListProducts(products);
                purchaseRequest.calculateAmount();
            } else {
                purchaseRequest = null;
            }

            prCreation.setPurchaseRequest(purchaseRequest);
        }

        endpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                String username = PRCreationSocket.users.get(endpoint.session.getId());

                if (username.equals(prCreation.getTo().getUsername())) {
                    try {
                        endpoint.session.getBasicRemote().
                                sendObject(prCreation);
                    } catch (IOException | EncodeException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}