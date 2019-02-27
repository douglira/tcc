package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controllers.socket.NotificationSocket;
import controllers.socket.QuoteNotifierSocket;
import dao.AddressDAO;
import dao.NotificationDAO;
import dao.ProductDAO;
import dao.PurchaseItemDAO;
import dao.PurchaseRequestDAO;
import dao.QuotationItemDAO;
import dao.QuoteDAO;
import dao.SellerDAO;
import dao.ShipmentDAO;
import dao.UserDAO;
import enums.MessengerType;
import enums.NotificationResource;
import enums.NotificationStatus;
import enums.PRStage;
import enums.QuoteStatus;
import enums.ShipmentMethod;
import enums.ShipmentStatus;
import libs.Helper;
import models.Address;
import models.Buyer;
import models.Item;
import models.Messenger;
import models.Person;
import models.Product;
import models.PurchaseRequest;
import models.Quote;
import models.Seller;
import models.Shipment;
import models.User;
import models.socket.Notification;
import org.apache.commons.lang.StringUtils;
import services.mail.MailSMTPService;
import services.mail.MailerService;
import services.mail.NewSuggestedQuote;

@SuppressWarnings("serial")
@WebServlet(name = "RestrictQuoteController", urlPatterns = {
        "/account/quote/new"
})
public class RestrictQuoteController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/quote/", "");

        switch (action) {
            case "new":
                create(request, response);
                break;
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                Helper.responseMessage(outError, new Messenger("Pedido de compra expirado", MessengerType.WARNING));
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

            quote.getCustomListProduct().forEach(quoteItem -> quotationItemDao.attachQuote(quote.getId(), quoteItem));

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
            System.out.println("RestrictQuoteController.doPost [ERROR]: " + err);
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
        if (Calendar.getInstance().after(purchaseRequest.getDueDate())) {
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
        String uri = request.getRequestURI();
        String action = uri.replace("/account/quote/", "");

        switch (action) {
            case "get":
                getById(request, response);
                break;
        }
    }

    private void getById(HttpServletRequest request, HttpServletResponse response) {
        String quoteId = request.getParameter("id");


        if (StringUtils.isBlank(quoteId)) {

        }


    }
}
