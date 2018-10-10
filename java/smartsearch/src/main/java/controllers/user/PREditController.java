package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PRCreationSocket;
import dao.ProductListDAO;
import dao.PurchaseRequestDAO;
import dao.SellerDAO;
import enums.MessengerType;
import libs.Helper;
import models.*;
import models.socket.PRCreation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "PREditController", urlPatterns = {"/account/purchase_request/edit"})
public class PREditController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        Messenger msg;

        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("purchaseRequestId"));
            int productItemId = Integer.parseInt(request.getParameter("productItemId"));
            ProductList productList = gJson.fromJson(request.getParameter("productList"), ProductList.class);

            ProductItem productItem = new ProductItem();
            productItem.setId(productItemId);
            productList.setProduct(productItem);

            ProductListDAO productListDao = new ProductListDAO(true);
            productListDao.initTransaction();
            productList.calculateAmount();
            productListDao.updateQuantityAndSpec(purchaseRequestId, productList);


            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setId(purchaseRequestId);

            purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);
            ArrayList<ProductList> products = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());

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

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            person.setUser(null);
            User user = (User) session.getAttribute("loggedUser");
            user.setPerson(person);

            PRCreation prCreation = new PRCreation(user);
            PRCreationSocket.sendPurchaseRequestUpdated(prCreation, Helper.getBaseUrl(request));

            out.print(gJson.toJson(new Messenger("Produto atualizado.", MessengerType.SUCCESS)));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PREditController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
