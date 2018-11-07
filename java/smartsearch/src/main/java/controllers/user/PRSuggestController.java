package controllers.user;

import com.google.gson.Gson;
import controllers.socket.NotificationSocket;
import controllers.socket.QuoteNotifierSocket;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

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

            if (validatePRExpiration(quote.getPurchaseRequest())) {
                Helper.responseMessage(outError, new Messenger("Este pedido de compra não se encontra sob cotação", MessengerType.WARNING));
                return;
            }

            if (validateQuoteExpirationInput(quote)) {
                Helper.responseMessage(outError, new Messenger("Data de expiração da cotação inválida", MessengerType.ERROR));
                return;
            }

            if (!validateSuggestedQuotes(quote.getPurchaseRequest().getId(), person.getId())) {
                Helper.responseMessage(outError, new Messenger("Limite de cotações sobre análise excedido. Aguarde um posicionamento do comprador", MessengerType.WARNING));
                return;
            }

            String validationShipmentMessage = validateShipmentOptions(quote.getShipmentOptions());

            if (validationShipmentMessage != null) {
                Helper.responseMessage(outError, new Messenger(validationShipmentMessage, MessengerType.ERROR));
                return;
            }

            quote.getPurchaseRequest().setListProducts(new PurchaseItemDAO(true).findByPurchaseRequest(quote.getPurchaseRequest().getId()));

            quote.getCustomListProduct().forEach(quoteItem -> {
                quoteItem.setProduct(new ProductDAO(true).findById(new Product(quoteItem.getProduct().getId())));
                quoteItem.calculateAmount();
            });

            Item item = validateProductsAvailability(quote.getCustomListProduct());

            if (item != null) {
                Helper.responseMessage(outError, new Messenger("Quantidade indisponível. PRODUTO: " + item.getProduct().getTitle(), MessengerType.ERROR));
                return;
            }

            Seller seller = new SellerDAO(true).findById(new Seller(person.getId()));
            seller.setCorporateName(person.getCorporateName());
            quote.setSeller(seller);

            quote.calculateTotalAmount();
            quote.setStatus(QuoteStatus.UNDER_REVIEW);
            quote.setCreatedAt(Calendar.getInstance());

            QuoteDAO quoteDao = new QuoteDAO(true);
            quoteDao.initTransaction();
            quoteDao.create(quote);

            QuotationItemDAO quotationItemDao = new QuotationItemDAO(quoteDao.getConnection());

            quote.getCustomListProduct().forEach(prodList -> quotationItemDao.attachQuote(quote.getId(), prodList));

            ShipmentDAO shipmentDao = new ShipmentDAO(quotationItemDao.getConnection());
            quote.getShipmentOptions().forEach(shipment -> {
                shipment.setStatus(ShipmentStatus.HANDLING);
                shipment.setQuote(new Quote(quote.getId()));

                if (!ShipmentMethod.LOCAL_PICK_UP.equals(shipment.getMethod())) {
                    setShippingReceiverAddress(shipment, quote.getPurchaseRequest());
                } else {
                    shipment.setCost(0);
                }

                shipment.setCreatedAt(Calendar.getInstance());
                shipmentDao.create(shipment);
            });

            shipmentDao.closeTransaction();
            shipmentDao.closeConnection();

            QuoteNotifierSocket.notifyUpdatedQuotes(quote.getPurchaseRequest());
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

    private void setShippingReceiverAddress(Shipment shipment, PurchaseRequest purchaseRequest) {
        Buyer buyer = purchaseRequest.getBuyer();

        Address receiverAddress = new AddressDAO(true).findByPerson(buyer.getId());
        shipment.setReceiverAddress(receiverAddress);
    }

    private boolean validateSuggestedQuotes(Integer purchaseRequestId, Integer sellerId) {
        ArrayList<Quote> quotes = new QuoteDAO(true).findByPurchaseRequest(purchaseRequestId);
        ArrayList<Quote> sellerQuotes = quotes.stream()
                .filter(quote -> sellerId.equals(quote.getSeller().getId()) && quote.getStatus().equals(QuoteStatus.UNDER_REVIEW))
                .collect(Collectors.toCollection(ArrayList::new));

        return sellerQuotes.size() < 3;
    }

    private boolean validateQuoteExpirationInput(Quote quote) {
        long now = Calendar.getInstance().getTimeInMillis();
        long quoteExpiration = quote.getExpirationDate().getTimeInMillis();
        long prExpiration = quote.getPurchaseRequest().getDueDate().getTimeInMillis();

        return quoteExpiration < now || quoteExpiration > prExpiration;
    }

    private boolean validatePRExpiration(PurchaseRequest purchaseRequest) {
        if (!purchaseRequest.getStage().equals(PRStage.UNDER_QUOTATION)) {
            return true;
        }

        if (purchaseRequest.getDueDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            new PurchaseRequestDAO(true).updateExpired(purchaseRequest);
            return true;
        }

        return false;
    }

    private String validateShipmentOptions(ArrayList<Shipment> shipments) {
        String validationMessage = null;

        if (shipments == null || shipments.isEmpty()) {
            validationMessage = "Nenhum método de envio especificado";
            return validationMessage;
        }

        long freeShippingCount = shipments.stream()
                .filter(ship -> ShipmentMethod.FREE.equals(ship.getMethod()))
                .count();

        if (freeShippingCount > 1) {
            validationMessage = "Adicione apenas um método de envio do mesmo tipo";
            return validationMessage;
        }

        long customShippingCount = shipments.stream()
                .filter(ship -> ShipmentMethod.CUSTOM.equals(ship.getMethod()))
                .count();

        if (customShippingCount > 1) {
            validationMessage = "Adicione apenas um método de envio do mesmo tipo";
            return validationMessage;
        }

        if (customShippingCount == 1 && freeShippingCount == 1) {
            validationMessage = "Métodos de envio inválidos";
            return validationMessage;
        }

        return validationMessage;
    }

    private Item validateProductsAvailability(ArrayList<Item> customListProduct) {
        return customListProduct.stream()
                .filter(quoteItem -> quoteItem.getQuantity() > ((Product) quoteItem.getProduct()).getAvailableQuantity())
                .findFirst()
                .orElse(null);
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
            ArrayList<Item> prProducts = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());

            prProducts.forEach(item -> {
                synchronized (item) {
                    ProductItem productItem = (ProductItem) item.getProduct();
                    productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                    productItem.setDefaultThumbnail(Helper.getBaseUrl(request));
                }
            });
            prProducts.sort(Item::compareTo);
            purchaseRequest.setListProducts(prProducts);

            if (purchaseRequest.getQuotesVisibility()) {
                ArrayList<Quote> quotes = new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                quotes.forEach(quote -> {
                    if (person.getId() == quote.getSeller().getId()) {
                        this.populateQuote(quote);
                    }
                });
                purchaseRequest.setQuotes(quotes);
            } else {
                ArrayList<Quote> restrictQuotes = new QuoteDAO(true).findRestrictQuotes(purchaseRequest.getId(), person.getId());
                restrictQuotes.forEach(this::populateQuote);
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

    private void populateQuote(Quote quote) {
        quote.setCustomListProduct(new QuotationItemDAO(true).findByQuote(quote.getId()));
        quote.setShipmentOptions(new ShipmentDAO(true).findByQuoteAndSeller(quote.getId(), quote.getSeller().getId()));
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
