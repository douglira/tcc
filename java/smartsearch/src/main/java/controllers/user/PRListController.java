package controllers.user;

import com.google.gson.Gson;
import dao.PurchaseRequestDAO;
import dao.QuoteDAO;
import enums.MessengerType;
import enums.PRStage;
import libs.Helper;
import models.Messenger;
import models.Person;
import models.PurchaseRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet(name = "PRListController", urlPatterns = {"/account/purchase_request/all"})
public class PRListController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        try {
            ArrayList<PurchaseRequest> purchaseRequests = new PurchaseRequestDAO(true).findByBuyer(person.getId());
            purchaseRequests.forEach(purchaseRequest -> purchaseRequest.setQuotes(new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId())));
            purchaseRequests = purchaseRequests.stream()
                    .filter(purchaseRequest -> !PRStage.CREATION.equals(purchaseRequest.getStage()))
                    .collect(Collectors.toCollection(ArrayList::new));

            out.print(gJson.toJson(purchaseRequests));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRSuggestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }
}
