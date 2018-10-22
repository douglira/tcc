package controllers.user;

import com.google.gson.Gson;
import controllers.socket.NotificationSocket;
import controllers.socket.QuoteSocket;
import dao.*;
import enums.*;
import libs.Helper;
import mail.MailSMTPService;
import mail.MailerService;
import mail.NewSuggestedQuote;
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
import java.util.ArrayList;
import java.util.Calendar;

@WebServlet(name = "PRSuggestController", urlPatterns = "/account/purchase_request/suggest")
public class PRSuggestController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(400);
        final PrintWriter outError = response.getWriter();
        Gson gson = new Gson();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loggedUser");
            Person person = (Person) session.getAttribute("loggedPerson");

            String quoteJson = request.getParameter("quote");
            Quote quote;
            try {
                quote = gson.fromJson(quoteJson, Quote.class);
            } catch (Exception err) {
                Helper.responseMessage(outError, new Messenger("Ops, dados inválidos. Revise sua cotação", MessengerType.ERROR));
                return;
            }

            quote.setPurchaseRequest(new PurchaseRequestDAO(true).findById(quote.getPurchaseRequest()));

            if (!quote.getPurchaseRequest().getStage().equals(PRStage.UNDER_QUOTATION)) {
                Helper.responseMessage(outError, new Messenger("Este pedido de compras não se encontra sob cotação", MessengerType.WARNING));
                return;
            }

            quote.getPurchaseRequest().setListProducts(new PRProductListDAO(true).findByPurchaseRequest(quote.getPurchaseRequest().getId()));

            quote.getCustomListProduct().forEach(productList -> {
                productList.setProduct(new ProductDAO(true).findById(new Product(productList.getProduct().getId())));
                productList.calculateAmount();
            });

            ProductList productList = quote.getCustomListProduct().stream()
                    .filter(prodList -> prodList.getQuantity() > ((Product) prodList.getProduct()).getAvailableQuantity())
                    .findFirst()
                    .orElse(null);

            if (productList != null) {
                Helper.responseMessage(outError, new Messenger("Quantidade indisponível. PRODUTO: " + productList.getProduct().getTitle(), MessengerType.ERROR));
                return;
            }

            Seller seller = new SellerDAO(true).findById(new Seller(person.getId()));
            seller.setCorporateName(person.getCorporateName());
            quote.setSeller(seller);

            quote.calculateTotalAmount();
            quote.setStatus(QuoteStatus.UNDER_REVIEW);
            quote.processExpirationDate();
            quote.setCreatedAt(Calendar.getInstance());

            QuoteDAO quoteDao = new QuoteDAO(true);
            quoteDao.initTransaction();
            quoteDao.create(quote);

            QuoteProductListDAO quoteProductListDao = new QuoteProductListDAO(quoteDao.getConnection());

            quote.getCustomListProduct().forEach(prodList -> quoteProductListDao.attachQuote(quote.getId(), prodList));

            quoteProductListDao.closeTransaction();
            quoteProductListDao.closeConnection();

            Runnable socketQuotes = null;
            if (quote.getPurchaseRequest().getQuotesVisibility()) {
                ArrayList<Quote> quotes = new QuoteDAO(true).findByPurchaseRequest(quote.getPurchaseRequest().getId());
                quotes.forEach(q -> {
                    if (q.getSeller().getId() == seller.getId()) {
                        q.setCustomListProduct(new QuoteProductListDAO(true).findByQuote(q.getId()));
                    }
                });
                socketQuotes = () -> QuoteSocket.sendUpdatedQuotes(quote.getPurchaseRequest(), quotes);
            } else {
                ArrayList<Quote> restrictQuotes = new QuoteDAO(true).findRestrictQuotes(quote.getPurchaseRequest().getId(), quote.getSeller().getId());
                restrictQuotes.forEach(restrictQuote -> restrictQuote.setCustomListProduct(new QuoteProductListDAO(true).findByQuote(restrictQuote.getId())));
                socketQuotes = () -> {
                    try {
                        QuoteSocket.sendUpdatedRestrictQuotes(user, quote.getPurchaseRequest(), restrictQuotes);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                };
            }

            new Thread(socketQuotes).start();
            sendNotificationsToBuyer(user, quote, getQuotesUrl(request, quote));

            response.setStatus(200);
            PrintWriter out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Cotação enviada com sucesso.", MessengerType.SUCCESS));
        } catch (Exception err) {
            err.printStackTrace();
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            System.out.println("PRSuggestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void sendNotificationsToBuyer(User sellerUser, Quote quote, String urlQuote) {
        User buyerUser = new UserDAO(true).findByPerson(quote.getPurchaseRequest().getBuyer().getId());

        final MailerService mailer = new NewSuggestedQuote(
                quote.getSeller().getCorporateName(),
                quote.getPurchaseRequest().getId(),
                quote.getTotalAmount(),
                urlQuote
        );
        mailer.setTo(buyerUser.getEmail());
        mailer.setMail(MailSMTPService.getInstance());

        Runnable mailTask = (mailer::send);

        Notification notification = new Notification();
        notification.setFrom(sellerUser);
        notification.setTo(buyerUser);
        notification.setResourceId(quote.getId());
        notification.setResourceType(NotificationResource.QUOTE);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setContent("[PEDIDO Nº" + quote.getPurchaseRequest().getId() + "]: " + "Cotação de " + NumberFormat.getCurrencyInstance().format(quote.getTotalAmount()) + " recebida");
        new NotificationDAO(true).create(notification);

        Runnable socketNotification = () -> NotificationSocket.pushLastNotifications(buyerUser);

        new Thread(mailTask).start();
        new Thread(socketNotification).start();
    }

    private String getQuotesUrl(HttpServletRequest request, Quote quote) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/purchase_request/quote?q=" + quote.getId();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        // Content Negotiation
        if (request.getHeader("Accept").contains("application/json")) {
            responseJson(request, response);
        } else {
            String purchaseRequestIdString = request.getParameter("pr");
            User user = (User) request.getSession().getAttribute("loggedUser");

            if (user == null || !isValidRequest(purchaseRequestIdString, person.getId())) {
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
            Person person = (Person) request.getSession().getAttribute("loggedPerson");
            String purchaseRequestIdString = request.getParameter("pr");

            if (!isValidRequest(purchaseRequestIdString, null)) {
                Helper.responseMessage(out, new Messenger(("Não foi possível carregar os dados"), MessengerType.ERROR, "INVALID_PURCHASE_REQUEST_ID"));
                return;
            }

            PurchaseRequest purchaseRequest = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));
            ArrayList<ProductList> prProducts = new PRProductListDAO(true).findByPurchaseRequest(purchaseRequest.getId());

            prProducts.forEach(prProduct -> {
                synchronized (prProduct) {
                    ProductItem productItem = (ProductItem) prProduct.getProduct();
                    productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                    productItem.setDefaultThumbnail(Helper.getBaseUrl(request));
                }
            });
            prProducts.sort(ProductList::compareTo);
            purchaseRequest.setListProducts(prProducts);

            if (purchaseRequest.getQuotesVisibility()) {
                ArrayList<Quote> quotes = new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                quotes.forEach(quote -> {
                    if (person.getId() == quote.getSeller().getId()) {
                        quote.setCustomListProduct(new QuoteProductListDAO(true).findByQuote(quote.getId()));
                    }
                });
                purchaseRequest.setQuotes(quotes);
            } else {
                ArrayList<Quote> restrictQuotes = new QuoteDAO(true).findRestrictQuotes(purchaseRequest.getId(), person.getId());
                restrictQuotes.forEach(restrictQuote -> restrictQuote.setCustomListProduct(new QuoteProductListDAO(true).findByQuote(restrictQuote.getId())));
                purchaseRequest.setQuotes(restrictQuotes);
            }

            out.print(gJson.toJson(purchaseRequest));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRSuggestController.doPost [ERROR]: " + error);
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

        if (buyerId != null && pr.getBuyer().getId() == buyerId) {
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
