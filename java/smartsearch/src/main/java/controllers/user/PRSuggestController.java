package controllers.user;

import com.google.gson.Gson;
import dao.FileDAO;
import dao.ProductListDAO;
import dao.PurchaseRequestDAO;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.ProductItem;
import models.ProductList;
import models.PurchaseRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "PRSuggestController", urlPatterns = "/account/purchase_request/suggest")
public class PRSuggestController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Content Negotiation
        if (request.getHeader("Accept").contains("application/json")) {
            responseJson(request, response);
        } else {
            String purchaseRequestIdString = request.getParameter("pr");

            if (!isInvalidRequest(purchaseRequestIdString)) {
                response.sendRedirect("/");
                return;
            }

            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-suggest.jsp").forward(request, response);
        }
    }

    private void responseJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            String purchaseRequestIdString = request.getParameter("pr");

            if (!isInvalidRequest(purchaseRequestIdString)) {
                Helper.responseMessage(out, new Messenger(("Não foi possível carregar os dados"), MessengerType.ERROR, "INVALID_PURCHASE_REQUEST_ID"));
                return;
            }

            PurchaseRequest purchaseRequest = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));
            ArrayList<ProductList> prProducts = new ProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());

            prProducts.forEach(prProduct -> {
                synchronized (prProduct) {
                    ProductItem productItem = (ProductItem) prProduct.getProduct();
                    productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                    productItem.setDefaultThumbnail(Helper.getBaseUrl(request));
                }
            });
            prProducts.sort(ProductList::compareTo);

            purchaseRequest.setListProducts(prProducts);

            out.print(gJson.toJson(purchaseRequest));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRSuggestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean isInvalidRequest(String purchaseRequestIdString) {
        boolean isValid = false;

        if (purchaseRequestIdString == null || purchaseRequestIdString.length() <= 4) {
            return isValid;
        }

        if (!isInteger(purchaseRequestIdString)) {
            return isValid;
        }

        PurchaseRequest pr = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));

        if (pr == null) {
            return isValid;
        }

        isValid = true;
        return isValid;
    }

    private boolean isInteger(String purchaseRequestIdString) {
        boolean isInteger = true;

        try {
            Integer id = Integer.parseInt(purchaseRequestIdString, 10);
        } catch (NumberFormatException error) {
            isInteger = false;
        }

        return isInteger;
    }
}
