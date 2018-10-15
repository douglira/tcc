package controllers.user;

import com.google.gson.Gson;
import controllers.socket.PurchaseRequestSocket;
import dao.ProductListDAO;
import dao.PurchaseRequestDAO;
import enums.MessengerType;
import libs.Helper;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "PRAbortController", urlPatterns = "/account/purchase_request/abort")
public class PRAbortController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            String action = request.getParameter("abortAction");

            if (action.equals("delete")) {
                String purchaseRequestId = request.getParameter("purchaseRequestId");
                User user = (User) request.getSession().getAttribute("loggedUser");
                Person person = (Person) request.getSession().getAttribute("loggedPerson");

                PurchaseRequest purchaseRequest = new PurchaseRequest();
                purchaseRequest.setId(Integer.parseInt(purchaseRequestId));
                purchaseRequest.setBuyer(new Buyer(person.getId()));
                purchaseRequestDelete(user , purchaseRequest);
            } else if (action.equals("canceled")) {
                // cancelar pedido de compra já publicado...
            }

            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PRAbortController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void purchaseRequestDelete(User user, PurchaseRequest purchaseRequest) throws SQLException {
        ProductListDAO productListDao = new ProductListDAO(true);
        productListDao.initTransaction();

        productListDao.removeAll(purchaseRequest.getId());
        new PurchaseRequestDAO(productListDao.getConnection()).destroyCreation(purchaseRequest.getId(), purchaseRequest.getBuyer().getId());

        purchaseRequest.setId(null);
        PurchaseRequestSocket.sendUpdatedPRCreation(user, purchaseRequest, null);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
