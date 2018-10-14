package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PurchaseRequestSocket;
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
        Gson gJson = new Gson();
        Messenger msg;

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loggedUser");
            Person person = (Person) session.getAttribute("loggedPerson");

            if (!isAuthenticated(response, person)) return;

            user.setPerson(person);

            String productItemId = request.getParameter("productItemId");
            String productItemQuantity = request.getParameter("productItemQuantity");
            String productItemAdditionalSpec = request.getParameter("productItemAdditionalSpec");

            if (productItemId == null || productItemId.length() == 0) {
                Helper.responseMessage(out, new Messenger("Operação inválida.", MessengerType.ERROR));
                return;
            }
            ProductItem productItem = new ProductItem();
            productItem.setId(Integer.parseInt(productItemId));

            if (isInvalidSellerPurchase(productItem, person)) {
                response.setStatus(400);
                Helper.responseMessage(response.getWriter(), new Messenger("Produto ainda disponível em seu estoque!", MessengerType.WARNING, "PRODUCT_AVAILABLE"));
                return;
            }

            updateViewsCount(Integer.parseInt(productItemId), Helper.getBaseUrl(request));

            ProductList productList = new ProductList();
            productList.setAdditionalSpec(productItemAdditionalSpec);
            productList.setProduct(productItem);

            if (productItemQuantity != null && productItemQuantity.length() > 0) {
                productList.setQuantity(Integer.parseInt(productItemQuantity));
            } else {
                productList.setQuantity(0);
            }

            addProductItem(out, Helper.getBaseUrl(request), user, productList);
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRCreationController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean isInvalidSellerPurchase(ProductItem productItem, Person person) {
        Product product = new ProductDAO(true).findByProductItemAndSeller(productItem.getId(), person.getId());
        return product != null && product.getAvailableQuantity() > 0;
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

    private void addProductItem(PrintWriter out, String baseUrl, User user, ProductList productList) throws SQLException {
        Gson gJson = new Gson();

        Buyer buyer = new Buyer();
        buyer.setId(user.getPerson().getId());

        ProductItem productItem = (ProductItem) productList.getProduct();
        productItem = new ProductItemDAO(true).findById(productItem);

        if (productItem == null) {
            Helper.responseMessage(out, new Messenger("Produto não encontrado.", MessengerType.WARNING));
            return;
        }
        productList.setProduct(productItem);
        productList.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(true);
        purchaseRequestDao.initTransaction();
        ProductListDAO productListDao = new ProductListDAO(purchaseRequestDao.getConnection());

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setBuyer(buyer);
        purchaseRequest.setStage(PRStage.CREATION);
        purchaseRequest.setQuotesVisibility(false);

        ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
        if (prs == null || prs.isEmpty()) {

            purchaseRequest.setViewsCount(0);
            purchaseRequest.setPropagationCount(0);
            purchaseRequest.addListProduct(productList);
            purchaseRequest.calculateAmount();
            purchaseRequest.setDueDateAverage(Calendar.getInstance());

            purchaseRequest = purchaseRequestDao.create(purchaseRequest);
        } else {
            purchaseRequest = prs.get(0);

            if (!new ProductListDAO(true).validateProductInsertion(purchaseRequest.getId(), productList)) {
                Helper.responseMessage(out, new Messenger("Produto já existente, altere sua quantidade ou remova-o.", MessengerType.WARNING));
                return;
            }
            ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());
            products.add(productList);
            products.sort(ProductList::compareTo);
            purchaseRequest.setListProducts(products);
            purchaseRequest.calculateAmount();

        }
        productListDao.attachPurchaseRequest(purchaseRequest.getId(), productList);

        ArrayList<Seller> sellers = new SellerDAO(productListDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());
        purchaseRequest.setPropagationCount(sellers.size());
        purchaseRequest.calculateDueDateAverage(sellers);
        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        pushSocketPurchaseRequest(user, purchaseRequest, baseUrl);

        out.print(gJson.toJson(purchaseRequest));
        out.close();
    }

    private void pushSocketPurchaseRequest(User user, PurchaseRequest purchaseRequest, String baseUrl) {
        ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());
        fetchPictures(products, baseUrl);

        purchaseRequest.setListProducts(products);

        user.setPerson(null);
        PurchaseRequestSocket.sendUpdatedPRCreation(user, purchaseRequest, baseUrl);
    }

    private void fetchPictures(ArrayList<ProductList> products, String baseUrl) {
        products.forEach(productList -> {
            synchronized (productList) {
                ProductItem productItem = (ProductItem) productList.getProduct();
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