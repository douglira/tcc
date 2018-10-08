package controllers;

import com.google.gson.Gson;
import dao.ProductItemDAO;
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
import java.util.Calendar;

@WebServlet(name = "PRCreationController", urlPatterns = {"/purchase_request/new"})
public class PRCreationController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        Messenger msg;

        try {
            String action = request.getParameter("actionCreation");

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            if (person == null || person.getId() <= 0) {
                Helper.responseMessage(out, new Messenger("Realize o login ou crie uma conta para montar seu pedido de compra.", MessengerType.WARNING, "WARNING_MISSING_LOGGED_USER"));
                return;
            }

            if (action == null || action.equals("single")) {
                String productItemId = request.getParameter("productItemId");
                String productItemQuantity = request.getParameter("productItemQuantity");
                String productItemAdditionalSpec = request.getParameter("productItemAdditionalSpec");

                if (productItemId == null || productItemId.length() == 0) {
                    Helper.responseMessage(out, new Messenger("Operação inválida.", MessengerType.ERROR));
                    return;
                }
                ProductItem productItem = new ProductItem();
                productItem.setId(Integer.parseInt(productItemId));

                ProductList productList = new ProductList();
                productList.setAdditionalSpec(productItemAdditionalSpec);
                productList.setProduct(productItem);
                if (productItemQuantity != null && productItemQuantity.length() > 0) {
                    productList.setQuantity(Integer.parseInt(productItemQuantity));
                } else {
                    productList.setQuantity(0);
                }

                singleCreation(out, person, productList);
                return;
            } else if (action.equals("bulk")) {
                // bulk creation
            }

            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRCreationController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void singleCreation(PrintWriter out, Person person, ProductList productList) throws SQLException {
        Gson gJson = new Gson();

        Buyer buyer = new Buyer();
        buyer.setId(person.getId());

        ProductItem productItem = (ProductItem) productList.getProduct();
        productItem = new ProductItemDAO(true).findById(productItem);

        if (productItem == null) {
            Helper.responseMessage(out, new Messenger("Produto inexistente na base de dados.", MessengerType.WARNING));
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

        out.print(gJson.toJson(purchaseRequest));
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}