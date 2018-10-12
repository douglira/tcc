package controllers.user;

import com.google.gson.Gson;
import dao.PersonDAO;
import dao.PurchaseRequestDAO;
import dao.SellerDAO;
import enums.MessengerType;
import enums.PRStage;
import libs.Helper;
import mail.MailSMTPService;
import mail.MailerService;
import mail.PublishedPurchaseRequest;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "PRPublishController", urlPatterns = {"/account/purchase_request/publish", "/account/purchase_request/details"})
public class PRPublishController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            PurchaseRequest purchaseRequest = gson.fromJson(request.getParameter("purchaseRequest"), PurchaseRequest.class);

            if (purchaseRequest.getPropagationCount() == 0) {
                Helper.responseMessage(out, new Messenger("Abrangência zerada!", MessengerType.WARNING));
                return;
            }
            purchaseRequest.setStage(PRStage.UNDER_QUOTATION);
            new PurchaseRequestDAO(true).updatePublish(purchaseRequest);

            SellerDAO sellerDao = new SellerDAO(true);
            ArrayList<Seller> sellers = sellerDao.findByPurchaseRequest(purchaseRequest.getId());
            sellerDao.closeConnection();

            ArrayList<User> sellerUsers = new ArrayList<User>();
            sellers.forEach(seller -> {
                Person person = new Person();
                person.setId(seller.getId());
                person = new PersonDAO(true).findByIdWithUser(person);

                sellerUsers.add(person.getUser());
            });

            final MailerService mailer = new PublishedPurchaseRequest(
                    purchaseRequest.getTotalAmount(),
                    getPurchaseRequestUrl(request, purchaseRequest));
            mailer.setMail(MailSMTPService.getInstance());

            Runnable runnable = () -> {
                mailNotification(mailer, sellerUsers);
                socketNotification(sellerUsers);
            };

            new Thread(runnable).start();

            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PRPublishController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void socketNotification(ArrayList<User> sellerUsers) {
        
    }

    private String getPurchaseRequestUrl(HttpServletRequest request, PurchaseRequest purchaseRequest) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/quotes/suggest?pr=" + String.valueOf(purchaseRequest.getId());
    }

    private void mailNotification(MailerService mailer, ArrayList<User> sellerUsers) {
        sellerUsers.forEach(sellerUser -> {
            synchronized (sellerUser) {
                mailer.setTo(sellerUser.getEmail());
                mailer.send();
            }
        });
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
