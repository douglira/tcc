package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

import com.google.gson.JsonObject;
import controllers.socket.NotificationSocket;
import controllers.socket.QuoteNotifierSocket;
import dao.*;
import enums.*;
import libs.Helper;
import models.*;
import models.socket.Notification;
import org.apache.commons.lang.StringUtils;
import services.elasticsearch.ElasticsearchService;
import services.mail.*;

@SuppressWarnings("serial")
@WebServlet(name = "RestrictQuoteController", urlPatterns = {
        // POST
        "/account/quote/new",
        "/account/quote/accept",
        "/account/quote/refuse",

        // GET
        "/account/quote/detail"
})
public class RestrictQuoteController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account", "");

        switch (action) {
            case "/quote/new": {
                create(request, response);
                break;
            }
            case "/quote/accept": {
                accepted(request, response);
            }
            case "/quote/refuse": {
                refuse(request, response);
            }
        }
    }

    /*
    -------------------- Exemplo de payload [QUOTE] -------------------
    {
        "purchaseRequest": {
            "id": 10030
        },
        "customListProduct": [{
            "product": {
                "id": 23,
                "title": "Playstation 4 Pro",
                "basePrice": 4249.9
            },
            "quantity": "1"
        }],
        "quoteAdditionalData": "",
        "discount": "5",
        "expirationDate": {
            "year": 2019,
            "month": 3,
            "dayOfMonth": 26,
            "hourOfDay": 0,
            "minute": 0,
            "second": 0
        },
        "shipmentOptions": [{
            "method": "CUSTOM",
            "estimatedTime": {
                "year": 2019,
                "month": 4,
                "dayOfMonth": 9,
                "hourOfDay": 0,
                "minute": 0,
                "second": 0
            },
            "cost": "41.50"
        }]
    }

     */
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

            if (quote.getPurchaseRequest().isExpired()) {
                new PurchaseRequestDAO(true).updateStage(quote.getPurchaseRequest());
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

            Item item = quote.validateProductsAvailability();

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
            notifyCreationToBuyer(user, quote, getQuotesUrl(request, quote));

            response.setStatus(200);
            PrintWriter out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Cotação enviada com sucesso.", MessengerType.SUCCESS));
        } catch (Exception err) {
            err.printStackTrace();
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            System.out.println("RestrictQuoteController.create [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void accepted(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(400);
        PrintWriter out = response.getWriter();

        try {
            String quoteId = request.getParameter("quoteId");
            String shipmentId = request.getParameter("shipmentId");
            Person buyerPerson = (Person) request.getSession().getAttribute("loggedPerson");

            if (StringUtils.isBlank(quoteId) || StringUtils.isBlank(shipmentId)) {
                Helper.responseMessage(out, new Messenger("Operação inválida", MessengerType.ERROR));
                return;
            }

            Quote quote = validateQuoteId(quoteId, buyerPerson);

            if (quote == null) {
                Helper.responseMessage(out, new Messenger("Operação inválida", MessengerType.ERROR));
                return;
            }

            populatePurchaseRequest(quote.getPurchaseRequest());

            if (!quote.getPurchaseRequest().getStage().equals(PRStage.UNDER_QUOTATION)) {
                Helper.responseMessage(out, new Messenger("Pedido de compra não se encontra sob cotação", MessengerType.ERROR));
                return;
            }

            if (!quote.getStatus().equals(QuoteStatus.UNDER_REVIEW)) {
                Helper.responseMessage(out, new Messenger("Cotação não se encontra sob análise", MessengerType.ERROR));
                return;
            }

            populateProductsAndShipments(quote, Helper.getBaseUrl(request));
            Shipment selectedShipment = quote.getShipmentOptions().stream()
                    .filter(shipment -> shipment.getId().equals(Integer.valueOf(Integer.parseInt(shipmentId))))
                    .findFirst()
                    .orElse(null);

            if (selectedShipment == null) {
                Helper.responseMessage(out, new Messenger("Método de envio inválido", MessengerType.ERROR));
                return;
            }

            Item quoteItem = quote.getCustomListProduct().stream()
                    .filter(quoteProduct -> quoteProduct.getQuantity() > ((Product) quoteProduct.getProduct()).getAvailableQuantity())
                    .findFirst()
                    .orElse(null);

            if (quoteItem != null) {
                notifyUnavailableQuantityToSeller(buyerPerson, quote, quoteItem, getPurchaseRequestUrl(request, quote.getPurchaseRequest()));
                Helper.responseMessage(out, new Messenger("COTAÇÂO ENCERRADA: Produto com quantidade insuficiente em estoque - " + ((Product) quoteItem.getProduct()).getTitle(), MessengerType.ERROR, "UNAVAILABLE_QUANTITY"));
                return;
            }

            ProductDAO productDao = new ProductDAO(true);
            productDao.initTransaction();
            updateInventory(quote, productDao.getConnection());

            Order order = new Order();
            order.setQuote(quote);
            order.setShipment(selectedShipment);
            order.setStatus(OrderStatus.CONFIRMED);
            order.calculateTotalAmount();
            new OrderDAO(productDao.getConnection()).create(order);
            productDao.getConnection().commit();
            productDao.getConnection().close();

            QuoteNotifierSocket.notifyUpdatedQuotes(order.getQuote().getPurchaseRequest());
            notifyApprovalToSeller(buyerPerson, order, getOrderUrl(request, order));

            response.setStatus(200);
            out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Cotação aceita com sucesso!", MessengerType.SUCCESS));
        } catch (Exception err) {
            err.printStackTrace();
            response.setStatus(500);
            out = response.getWriter();
            System.out.println("RestrictQuoteController.accepted [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void refuse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(400);
        PrintWriter out = response.getWriter();

        try {
            String quoteId = request.getParameter("quoteId");
            String quoteReason = request.getParameter("quoteReason");
            Person buyerPerson = (Person) request.getSession().getAttribute("loggedPerson");

            if (StringUtils.isBlank(quoteReason)) {
                Helper.responseMessage(out, new Messenger("Obrigatório descrever o motivo da negação desta cotação", MessengerType.ERROR));
                return;
            }

            Quote quote = validateQuoteId(quoteId, buyerPerson);

            if (quote == null) {
                Helper.responseMessage(out, new Messenger("Operação inválida", MessengerType.ERROR));
                return;
            }

            if (quote.getStatus().equals(QuoteStatus.UNDER_REVIEW) && quote.isExpired()) {
                new QuoteDAO(true).updateStatus(quote);
                Helper.responseMessage(out, new Messenger("Esta cotação não é válida", MessengerType.ERROR));
                return;
            }

            quote.setStatus(QuoteStatus.DECLINED);
            quote.setReason(quoteReason);
            new QuoteDAO(true).updateStatusAndReason(quote);

            QuoteNotifierSocket.notifyUpdatedQuotes(quote.getPurchaseRequest());
            notifyDenialToSeller(buyerPerson, quote, getPurchaseRequestUrl(request, quote.getPurchaseRequest()));

            response.setStatus(200);
            out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Cotação recusada!", MessengerType.SUCCESS));
        } catch (Exception err) {
            err.printStackTrace();
            response.setStatus(500);
            out = response.getWriter();
            System.out.println("RestrictQuoteController.refuse [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void notifyCreationToBuyer(User sellerUser, Quote quote, String urlQuote) {
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

    private void notifyApprovalToSeller(Person buyerPerson, Order order, String urlOrder) {
        User sellerUser = new UserDAO(true).findByPerson(order.getQuote().getSeller().getId());

        final MailerService mailer = new ApprovedSuggestedQuote(
                buyerPerson.getAccountOwner(),
                order.getId(),
                order.getTotalAmount(),
                urlOrder
        );
        mailer.setTo(sellerUser.getEmail());
        mailer.setMail(MailSMTPService.getInstance());

        Runnable mailTask = (mailer::send);

        Notification notification = new Notification();
        notification.setFrom(buyerPerson.getUser());
        notification.setTo(sellerUser);
        notification.setResourceId(order.getId());
        notification.setResourceType(NotificationResource.ORDER);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setContent(
            new StringBuilder()
                .append("[COTAÇÃO ACEITA]: Sua cotação feita para ")
                .append(buyerPerson.getAccountOwner())
                .append(" foi aprovada!!!")
                .toString()
        );
        new NotificationDAO(true).create(notification);

        Runnable socketNotification = () -> NotificationSocket.pushLastNotifications(sellerUser);

        new Thread(mailTask).start();
        new Thread(socketNotification).start();
    }

    private void notifyDenialToSeller(Person buyerPerson, Quote quote, String urlPurchaseRequest) {
        User sellerUser = new UserDAO(true).findByPerson(quote.getSeller().getId());

        final MailerService mailer = new RefusedSuggestedQuote(
                buyerPerson.getAccountOwner(),
                quote.getReason(),
                quote.getPurchaseRequest().getId(),
                quote.getTotalAmount(),
                urlPurchaseRequest
        );
        mailer.setTo(sellerUser.getEmail());
        mailer.setMail(MailSMTPService.getInstance());

        Runnable mailTask = (mailer::send);

        Notification notification = new Notification();
        notification.setFrom(buyerPerson.getUser());
        notification.setTo(sellerUser);
        notification.setResourceId(quote.getPurchaseRequest().getId());
        notification.setResourceType(NotificationResource.PURCHASE_REQUEST);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setContent(
            new StringBuilder()
                .append("[COTAÇÃO RECUSADA]: Sua cotação de ")
                .append(NumberFormat.getCurrencyInstance().format(quote.getTotalAmount()))
                .append(" do pedido Nº")
                .append(quote.getPurchaseRequest().getId())
                .append(" foi recusada :(")
                .toString()
        );
        new NotificationDAO(true).create(notification);

        Runnable socketNotification = () -> NotificationSocket.pushLastNotifications(sellerUser);

        new Thread(mailTask).start();
        new Thread(socketNotification).start();
    }

    private void notifyUnavailableQuantityToSeller(Person buyerPerson, Quote quote, Item unavailableQuoteItem, String urlPurchaseRequest) {
        String quoteReason = new StringBuilder()
                .append("PRODUTO COM QUANTIDADE INSUFICIENTE: ")
                .append(((Product) unavailableQuoteItem.getProduct()).getTitle())
                .append(" - ")
                .append("Quantidade em estoque: ")
                .append(((Product) unavailableQuoteItem.getProduct()).getAvailableQuantity())
                .append(" - ")
                .append("Quantidade cotada: ")
                .append(unavailableQuoteItem.getQuantity())
                .toString();
        quote.setReason(quoteReason);
        quote.setStatus(QuoteStatus.DECLINED);
        new QuoteDAO(true).updateStatusAndReason(quote);

        QuoteNotifierSocket.notifyUpdatedQuotes(quote.getPurchaseRequest());
        notifyDenialToSeller(buyerPerson, quote, urlPurchaseRequest);
    }

    private void updateInventory(Quote quote, Connection connection) {
        ProductDAO productDao = new ProductDAO(connection);
        quote.getCustomListProduct().forEach(quoteProduct -> {
            Product product = (Product) quoteProduct.getProduct();
            product.updateSale(quoteProduct.getQuantity());
            productDao.updateSale(product);


            if (product.getAvailableQuantity() == 0) {
                ProductItem productItem = new ProductItemDAO(true).findByProduct(product.getId());

                if (productItem != null) {
                    productItem.setBasedProducts(new ProductDAO(true).findByProductItem(productItem.getId()));
                    productItem.setRelevance(productItem.getRelevance() - 1);
                    productItem.updatePrices();
                    new ProductItemDAO(productDao.getConnection()).updatePricesAndRelevance(productItem);
                    new ElasticsearchService().updateProductItemPricesAndRelevance(productItem);
                }
            }
            quoteProduct.setProduct(product);
        });
        new QuoteDAO(productDao.getConnection()).updateAcceptedStatus(quote);
        new PurchaseRequestDAO(productDao.getConnection()).updateClosedStage(quote.getPurchaseRequest());
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
        Calendar now = Calendar.getInstance();
        Calendar quoteExpiration = quote.getExpirationDate();
        Calendar prExpiration = quote.getPurchaseRequest().getDueDate();

        return quoteExpiration.before(now) || quoteExpiration.after(prExpiration);
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

    private String getOrderUrl(HttpServletRequest request, Order order) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/order/detail?order=" + String.valueOf(order.getId());
    }

    private String getQuotesUrl(HttpServletRequest request, Quote quote) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/quote/detail?q=" + quote.getId();
    }

    private String getPurchaseRequestUrl(HttpServletRequest request, PurchaseRequest purchaseRequest) {
        String baseUrl = Helper.getBaseUrl(request);
        return baseUrl + "/account/purchase_request/suggest?pr=" + String.valueOf(purchaseRequest.getId());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account", "");

        switch (action) {
            case "/quote/detail":
                getById(request, response);
                break;
        }
    }

    private void getById(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getHeader("Accept").contains("application/json")) {
            jsonGetById(request, response);
        } else {
            renderGetById(request, response);
        }
    }

    private void renderGetById(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String quoteId = request.getParameter("q");
        Person person = (Person) request.getSession().getAttribute("loggedPerson");
        Quote quote = validateQuoteId(quoteId, person);

        if (quote == null) {
            response.sendRedirect("/");
            return;
        }

        if (quote.getStatus().equals(QuoteStatus.UNDER_REVIEW) && quote.isExpired()) {
            new QuoteDAO(true).updateStatus(quote);
        }

        request.getRequestDispatcher(request.getContextPath() + "/user/quote-detail.jsp").forward(request, response);
    }

    private void jsonGetById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        try {
            String quoteId = request.getParameter("q");
            Person person = (Person) request.getSession().getAttribute("loggedPerson");
            Quote quote = validateQuoteId(quoteId, person);

            if (quote == null) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Operação inválida.", MessengerType.ERROR));
                return;
            }

            Person sellerPerson = getQuoteSeller(quote);
            populatePurchaseRequest(quote.getPurchaseRequest());
            populateProductsAndShipments(quote, Helper.getBaseUrl(request));

            JsonObject json = new JsonObject();
            json.add("quote", gJson.toJsonTree(quote));
            json.add("seller", gJson.toJsonTree(sellerPerson));
            out.print(json.toString());
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            response.setStatus(500);
            out = response.getWriter();
            System.out.println("RestrictQuoteController.jsonGetById [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private Person getQuoteSeller(Quote quote) {
        Person sellerPerson = (Person) quote.getSeller();
        return new PersonDAO(true).findById(sellerPerson);
    }

    private void populateProductsAndShipments(Quote quote, String baseUrl) {
        quote.setCustomListProduct(
                new QuotationItemDAO(true).findByQuote(quote.getId())
                .stream()
                .peek(quotationItem -> {
                    quotationItem.getProduct().setPictures(new FileDAO(true).getProductPictures(quotationItem.getProduct().getId()));
                    quotationItem.getProduct().setDefaultThumbnail(baseUrl);
                    quotationItem.calculateAmount();
                })
                .collect(Collectors.toCollection(ArrayList::new))
        );
        quote.setShipmentOptions(
                new ShipmentDAO(true).findByQuoteAndSeller(quote.getId(), quote.getSeller().getId())
                .stream()
                .peek(shipment -> shipment.setReceiverAddress(new AddressDAO(true).findById(shipment.getReceiverAddress())))
                .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    private void populatePurchaseRequest(PurchaseRequest purchaseRequest) {
        purchaseRequest.setListProducts(new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId()));
    }

    private Quote validateQuoteId(String quoteId, Person person) {
        Quote quote = null;

        if (StringUtils.isBlank(quoteId)) {
            return quote;
        }

        if (!Helper.isInteger(quoteId)) {
            return quote;
        }

        quote = new QuoteDAO(true).findById(new Quote(Integer.parseInt(quoteId)));

        if (quote == null) {
            return quote;
        }

        quote.setPurchaseRequest(new PurchaseRequestDAO(true).findById(quote.getPurchaseRequest()));

        if (quote.getPurchaseRequest().getBuyer().getId() != person.getId()) {
            quote = null;
            return quote;
        }

        return quote;
    }
}
