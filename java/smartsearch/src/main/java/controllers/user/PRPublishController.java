package controllers.user;

import com.google.gson.Gson;
import controllers.socket.NotificationSocket;
import dao.*;
import enums.MessengerType;
import enums.NotificationStatus;
import enums.NotificationResource;
import enums.PRStage;
import libs.Helper;
import mail.MailSMTPService;
import mail.MailerService;
import mail.PublishedPurchaseRequest;
import models.*;
import models.socket.Notification;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@WebServlet(name = "PRPublishController", urlPatterns = {"/account/purchase_request/publish", "/account/purchase_request/details"})
public class PRPublishController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            PurchaseRequest purchaseRequest = gson.fromJson(request.getParameter("purchaseRequest"), PurchaseRequest.class);

            if (validateDueDate(purchaseRequest)) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Data de expiração inválida", MessengerType.ERROR));
                return;
            }

            if ((new PurchaseRequestDAO(true).findById(new PurchaseRequest(purchaseRequest.getId()))).getPropagationCount() == 0) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Abrangência zerada!", MessengerType.ERROR));
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

            Runnable socketTask = () -> socketNotification(sellerUsers, purchaseRequest);
            Runnable mailTask = () -> mailNotification(mailer, sellerUsers);
            new Thread(socketTask).start();
            new Thread(mailTask).start();

            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PRPublishController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean validateDueDate(PurchaseRequest purchaseRequest) throws ParseException {
        Date dueDate = purchaseRequest.getDueDate().getTime();
        Date now = new Date();

        long diff = dueDate.getTime() - now.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(dueDate);
        purchaseRequest.setDueDate(dueDateCalendar);

        return days > 90 || days <= 0;
    }

    private void socketNotification(ArrayList<User> sellerUsers, PurchaseRequest purchaseRequest) {
        sellerUsers.forEach(sellerUser -> {
            synchronized (sellerUser) {
                Notification notification = new Notification();
                notification.setFrom(null);
                notification.setTo(sellerUser);
                notification.setResourceId(purchaseRequest.getId());
                notification.setResourceType(NotificationResource.PURCHASE_REQUEST);
                notification.setStatus(NotificationStatus.PENDING);
                notification.setContent("Novo pedido de compra encontrado para você no valor de " + NumberFormat.getCurrencyInstance().format(purchaseRequest.getTotalAmount()));

                new NotificationDAO(true).create(notification);
                NotificationSocket.pushLastNotifications(sellerUser);
            }
        });
    }

    private void mailNotification(MailerService mailer, ArrayList<User> sellerUsers) {
        sellerUsers.forEach(sellerUser -> {
            synchronized (sellerUser) {
                mailer.setTo(sellerUser.getEmail());
                mailer.send();
            }
        });
    }

    private String getPurchaseRequestUrl(HttpServletRequest request, PurchaseRequest purchaseRequest) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/purchase_request/suggest?pr=" + String.valueOf(purchaseRequest.getId());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        String purchaseRequestIdString = request.getParameter("id");

        if (person == null || !isValidRequest(purchaseRequestIdString, person.getId())) {
            response.sendRedirect("/");
            return;
        }

        if (request.getHeader("Accept").contains("application/json")) {
            responseJson(request, response);
        } else {
            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-details.jsp").forward(request, response);
        }
    }

    private void responseJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            PurchaseRequest purchaseRequest = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(request.getParameter("id"))));
            purchaseRequest.setQuotes(new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId()));
            purchaseRequest.setListProducts(new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId()));

            out.print(gson.toJson(purchaseRequest));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("PRPublishController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean isValidRequest(String purchaseRequestIdString, Integer buyerId) {
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

        if (buyerId == null || !buyerId.equals(pr.getBuyer().getId())) {
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
