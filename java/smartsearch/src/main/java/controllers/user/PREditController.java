package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PurchaseRequestSocket;
import dao.FileDAO;
import dao.ProductListDAO;
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

                ProductList productList = gJson.fromJson(request.getParameter("productList"), ProductList.class);
                updatePurchaseRequestItem(user, purchaseRequestId, productItemId, productList, Helper.getBaseUrl(request));
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

    private void updatePurchaseRequestItem(User user, int purchaseRequestId, int productItemId, ProductList productList, String baseUrl) throws SQLException {
        ProductItem productItem = new ProductItem();
        productItem.setId(productItemId);
        productList.setProduct(productItem);

        ProductListDAO productListDao = new ProductListDAO(true);
        productListDao.initTransaction();
        productList.calculateAmount();
        productListDao.updateQuantityAndSpec(purchaseRequestId, productList);

        updatePurchaseRequestData(user, purchaseRequestId, productListDao, baseUrl);
    }

    private void removePurchaseRequestItem(User user, int purchaseRequestId, int productItemId, String baseUrl) throws SQLException {
        ProductListDAO productListDao = new ProductListDAO(true);
        productListDao.initTransaction();
        productListDao.remove(purchaseRequestId, productItemId);

        ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequestId);
        if (products.isEmpty()) {
            PurchaseRequest pr = new PurchaseRequest();
            new PurchaseRequestDAO(productListDao.getConnection()).destroy(purchaseRequestId, user.getPerson().getId());

            pr.setId(null);
            PurchaseRequestSocket.sendUpdatedPRCreation(user, pr, null);
            return;
        }

        updatePurchaseRequestData(user, purchaseRequestId, productListDao, baseUrl);
    }

    private void updatePurchaseRequestData(User user, int purchaseRequestId, ProductListDAO productListDao, String baseUrl) throws SQLException {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setId(purchaseRequestId);

        purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);
        ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());

        products.sort(ProductList::compareTo);
        purchaseRequest.setListProducts(products);
        purchaseRequest.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(productListDao.getConnection());
        purchaseRequestDao.updateTotalAmount(purchaseRequest);
        ArrayList<Seller> sellers = new SellerDAO(productListDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());

        purchaseRequest.setPropagationCount(sellers.size());
        purchaseRequest.calculateDueDateAverage(sellers);

        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        PurchaseRequestSocket.sendUpdatedPRCreation(user, null, baseUrl);
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

                ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                products.forEach(productList -> {
                    synchronized (productList) {
                        ProductItem productItem = (ProductItem) productList.getProduct();
                        productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                        productItem.setDefaultThumbnail(Helper.getBaseUrl(request));
                    }
                });

                products.sort(ProductList::compareTo);
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
