package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PRCreationSocket;
import dao.*;
import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import enums.PRStage;
import libs.Helper;
import models.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

@WebServlet(name = "PRCreationController", urlPatterns = {"/purchase_request/new", "/account/purchase_request/new"})
public class PRCreationController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loggedUser");
            Person person = (Person) session.getAttribute("loggedPerson");

            if (!isAuthenticated(response, person)) return;

            user.setPerson(person);

            if (missingAddressRegister(user)) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Cadastre seu endereço para criar um pedido de compra", MessengerType.WARNING, "ERROR_MISSING_ADDRESS"));
                return;
            }

            String productItemId = request.getParameter("productItemId");
            String productItemQuantity = request.getParameter("productItemQuantity");
            String productItemAdditionalSpec = request.getParameter("productItemAdditionalSpec");

            if (productItemId == null || productItemId.length() == 0) {
                Helper.responseMessage(out, new Messenger("Operação inválida.", MessengerType.ERROR));
                return;
            }
            ProductItem productItem = new ProductItem();
            productItem.setId(Integer.parseInt(productItemId));

            updateViewsCount(Integer.parseInt(productItemId), Helper.getBaseUrl(request));

            Item item = new Item();
            item.setAdditionalSpec(productItemAdditionalSpec);
            item.setProduct(productItem);

            if (productItemQuantity != null && productItemQuantity.length() > 0) {
                item.setQuantity(Integer.parseInt(productItemQuantity));
            } else {
                item.setQuantity(0);
            }

            addProductItem(out, Helper.getBaseUrl(request), user, item);
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRCreationController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean missingAddressRegister(User user) {
        Address address = new AddressDAO(true).findByPerson(user.getPerson().getId());

        return address == null;
    }

    private void updateViewsCount(int productItemId , String baseUrl) {
        ProductItem productItem = new ProductItem();
        productItem.setId(productItemId);

        productItem = new ProductItemDAO(true).findById(productItem);
        productItem.setViewsCount(productItem.getViewsCount() + 1);
        new ProductItemDAO(true).updateViewsCount(productItem);
        productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
        productItem.setDefaultThumbnail(baseUrl);

        new ElasticsearchFacade().indexProductItem(productItem);
    }

    private void addProductItem(PrintWriter out, String baseUrl, User user, Item item) throws SQLException {
        Gson gJson = new Gson();

        Buyer buyer = new Buyer();
        buyer.setId(user.getPerson().getId());

        ProductItem productItem = (ProductItem) item.getProduct();
        productItem = new ProductItemDAO(true).findById(productItem);

        if (productItem == null) {
            Helper.responseMessage(out, new Messenger("Produto não encontrado.", MessengerType.WARNING));
            return;
        }
        item.setProduct(productItem);
        item.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(true);
        purchaseRequestDao.initTransaction();
        PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(purchaseRequestDao.getConnection());

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setBuyer(buyer);
        purchaseRequest.setStage(PRStage.CREATION);
        purchaseRequest.setQuotesVisibility(false);

        ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
        if (prs == null || prs.isEmpty()) {

            purchaseRequest.setViewsCount(0);
            purchaseRequest.setPropagationCount(0);
            purchaseRequest.addListProduct(item);
            purchaseRequest.calculateAmount();
            purchaseRequest.setDueDate(Calendar.getInstance());

            purchaseRequest = purchaseRequestDao.create(purchaseRequest);
        } else {
            purchaseRequest = prs.get(0);

            if (!new PurchaseItemDAO(true).validateProductInsertion(purchaseRequest.getId(), item)) {
                Helper.responseMessage(out, new Messenger("Produto já existente, altere sua quantidade ou remova-o.", MessengerType.WARNING));
                return;
            }
            ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
            products.add(item);
            products.sort(Item::compareTo);
            purchaseRequest.setListProducts(products);
            purchaseRequest.calculateAmount();

        }
        PurchaseItemDao.attachPurchaseRequest(purchaseRequest.getId(), item);

        ArrayList<Seller> sellers = new SellerDAO(PurchaseItemDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());
        purchaseRequest.setPropagationCount(sellers.size());
        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        pushSocketPurchaseRequest(user, purchaseRequest, baseUrl);

        out.print(gJson.toJson(purchaseRequest));
        out.close();
    }

    private void pushSocketPurchaseRequest(User user, PurchaseRequest purchaseRequest, String baseUrl) {
        ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
        fetchPictures(products, baseUrl);

        purchaseRequest.setListProducts(products);

        user.setPerson(null);
        PRCreationSocket.sendUpdatedPRCreation(user, purchaseRequest, baseUrl);
    }

    private void fetchPictures(ArrayList<Item> products, String baseUrl) {
        products.forEach(item -> {
            synchronized (item) {
                ProductItem productItem = (ProductItem) item.getProduct();
                productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                productItem.setDefaultThumbnail(baseUrl);
            }
        });
    }

    private boolean isAuthenticated(HttpServletResponse response, Person person) throws IOException {
        if (person == null || person.getId() <= 0) {
            response.setStatus(401);
            PrintWriter out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Realize o login ou crie uma conta para montar seu pedido de compra.", MessengerType.WARNING, "WARNING_MISSING_LOGGED_USER"));
            return false;
        }
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String purchaseRequestId = request.getParameter("pr");

            if (purchaseRequestId == null || purchaseRequestId.length() < 4) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setId(Integer.parseInt(purchaseRequestId));
            purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);

            if (purchaseRequest == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-creation.jsp").forward(request, response);
        } catch (Exception err) {
            err.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}