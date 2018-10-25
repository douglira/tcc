package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PRCreationSocket;
import dao.FileDAO;
import dao.PurchaseItemDAO;
import dao.PurchaseRequestDAO;
import dao.SellerDAO;
import enums.MessengerType;
import enums.PRStage;
import libs.Helper;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "PREditController", urlPatterns = {"/account/purchase_request/edit"})
public class PREditController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            String action = request.getParameter("action");
            int purchaseRequestId = Integer.parseInt(request.getParameter("purchaseRequestId"));
            int productItemId = Integer.parseInt(request.getParameter("productItemId"));

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            person.setUser(null);
            User user = (User) session.getAttribute("loggedUser");
            user.setPerson(person);

            if (action.equals("update")) {

                Item item = gJson.fromJson(request.getParameter("purchaseItem"), Item.class);
                updatePurchaseRequestItem(user, purchaseRequestId, productItemId, item, Helper.getBaseUrl(request));
            } else if (action.equals("remove")) {
                removePurchaseRequestItem(user, purchaseRequestId, productItemId, Helper.getBaseUrl(request));
            }

            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PREditController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void updatePurchaseRequestItem(User user, int purchaseRequestId, int productItemId, Item item, String baseUrl) throws SQLException {
        if (item.getQuantity() <= 0) {
            return;
        }

        ProductItem productItem = new ProductItem();
        productItem.setId(productItemId);
        item.setProduct(productItem);

        PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(true);
        PurchaseItemDao.initTransaction();
        item.calculateAmount();
        PurchaseItemDao.updateQuantityAndSpec(purchaseRequestId, item);

        updatePurchaseRequestData(user, purchaseRequestId, PurchaseItemDao, baseUrl);
    }

    private void removePurchaseRequestItem(User user, int purchaseRequestId, int productItemId, String baseUrl) throws SQLException {
        PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(true);
        PurchaseItemDao.initTransaction();
        PurchaseItemDao.remove(purchaseRequestId, productItemId);

        ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequestId);
        if (products.isEmpty()) {
            PurchaseRequest pr = new PurchaseRequest();
            new PurchaseRequestDAO(PurchaseItemDao.getConnection()).destroyCreation(purchaseRequestId, user.getPerson().getId());

            pr.setId(null);
            PRCreationSocket.sendUpdatedPRCreation(user, pr, null);
            return;
        }

        updatePurchaseRequestData(user, purchaseRequestId, PurchaseItemDao, baseUrl);
    }

    private void updatePurchaseRequestData(User user, int purchaseRequestId, PurchaseItemDAO PurchaseItemDao, String baseUrl) throws SQLException {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setId(purchaseRequestId);

        purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);
        ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());

        products.sort(Item::compareTo);
        purchaseRequest.setListProducts(products);
        purchaseRequest.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(PurchaseItemDao.getConnection());
        purchaseRequestDao.updateTotalAmount(purchaseRequest);
        ArrayList<Seller> sellers = new SellerDAO(PurchaseItemDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());

        purchaseRequest.setPropagationCount(sellers.size());

        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        PRCreationSocket.sendUpdatedPRCreation(user, null, baseUrl);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            Buyer buyer = new Buyer();
            buyer.setId(person.getId());

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setBuyer(buyer);
            purchaseRequest.setStage(PRStage.CREATION);

            ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
            if (prs != null && !prs.isEmpty()) {
                purchaseRequest = prs.get(0);

                ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                products.forEach(item -> {
                    synchronized (item) {
                        ProductItem productItem = (ProductItem) item.getProduct();
                        productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                        productItem.setDefaultThumbnail(Helper.getBaseUrl(request));
                    }
                });

                products.sort(Item::compareTo);
                purchaseRequest.setListProducts(products);
                purchaseRequest.calculateAmount();
            } else {
                purchaseRequest = null;
            }

            out.print(gJson.toJson(purchaseRequest));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PRCreationController.doGet [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }
}
