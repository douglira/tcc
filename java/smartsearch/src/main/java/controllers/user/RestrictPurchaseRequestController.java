package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import enums.*;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import controllers.socket.NotificationSocket;
import controllers.socket.PRCreationSocket;
import dao.AddressDAO;
import dao.FileDAO;
import dao.NotificationDAO;
import dao.PersonDAO;
import dao.ProductItemDAO;
import dao.PurchaseItemDAO;
import dao.PurchaseRequestDAO;
import dao.QuotationItemDAO;
import dao.QuoteDAO;
import dao.SellerDAO;
import dao.ShipmentDAO;
import dao.UserDAO;
import libs.Helper;
import models.Address;
import models.Buyer;
import models.Item;
import models.Messenger;
import models.Person;
import models.ProductItem;
import models.PurchaseRequest;
import models.Quote;
import models.Seller;
import models.User;
import models.socket.Notification;
import services.elasticsearch.ElasticsearchService;
import services.mail.MailSMTPService;
import services.mail.MailerService;
import services.mail.PublishedPurchaseRequest;

@SuppressWarnings("serial")
@WebServlet(name = "RestrictPurchaseRequestController", urlPatterns = {
        "/account/purchase_request",
        "/account/purchase_request/list",
        "/account/purchase_request/creation",
        "/account/purchase_request/details",

        "/account/purchase_request/new",
        "/account/purchase_request/save",
        "/account/purchase_request/remove",
        "/account/purchase_request/abort",
        "/account/purchase_request/publish",
        "/account/purchase_request/suggest"
})
public class RestrictPurchaseRequestController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/purchase_request/", "");

        switch (action) {
            case "new":
                create(request, response);
                break;
            case "save":
                saveItem(request, response);
                break;
            case "remove":
                removeItem(request, response);
                break;
            case "abort":
                abortCreation(request, response);
                break;
            case "publish":
                publish(request, response);
                break;
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loggedUser");
            Person person = (Person) session.getAttribute("loggedPerson");
            user.setPerson(person);

            if (missingAddressRegister(user)) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Cadastre seu endereço para criar um pedido de compra", MessengerType.WARNING, "ERROR_MISSING_ADDRESS"));
                return;
            }

            String productItemId = request.getParameter("productItemId");
            String productItemQuantity = request.getParameter("productItemQuantity");
            String productItemAdditionalSpec = request.getParameter("productItemAdditionalSpec");

            if (productItemId == null || productItemId.length() == 0) {
                Helper.responseMessage(out, new Messenger("Operação inválida.", MessengerType.ERROR));
                return;
            }
            ProductItem productItem = new ProductItem();
            productItem.setId(Integer.parseInt(productItemId));

            updateViewsCount(Integer.parseInt(productItemId), Helper.getBaseUrl(request));

            Item item = new Item();
            item.setAdditionalSpec(productItemAdditionalSpec);
            item.setProduct(productItem);

            if (productItemQuantity != null && productItemQuantity.length() > 0) {
                item.setQuantity(Integer.parseInt(productItemQuantity));
            } else {
                item.setQuantity(0);
            }

            addProductItem(out, Helper.getBaseUrl(request), user, item);
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void saveItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("purchaseRequestId"));
            int productItemId = Integer.parseInt(request.getParameter("productItemId"));

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            person.setUser(null);
            User user = (User) session.getAttribute("loggedUser");
            user.setPerson(person);

            Item item = gJson.fromJson(request.getParameter("purchaseItem"), Item.class);

            if (item.getQuantity() <= 0) {
                return;
            }

            ProductItem productItem = new ProductItem();
            productItem.setId(productItemId);
            item.setProduct(productItem);

            PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(true);
            PurchaseItemDao.initTransaction();
            item.calculateAmount();
            PurchaseItemDao.updateQuantityAndSpec(purchaseRequestId, item);

            updatePurchaseRequestData(user, purchaseRequestId, PurchaseItemDao, Helper.getBaseUrl(request));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void removeItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int purchaseRequestId = Integer.parseInt(request.getParameter("purchaseRequestId"));
            int productItemId = Integer.parseInt(request.getParameter("productItemId"));

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            person.setUser(null);
            User user = (User) session.getAttribute("loggedUser");
            user.setPerson(person);

            PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(true);
            PurchaseItemDao.initTransaction();
            PurchaseItemDao.remove(purchaseRequestId, productItemId);

            ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequestId);
            if (products.isEmpty()) {
                PurchaseRequest pr = new PurchaseRequest();
                new PurchaseRequestDAO(PurchaseItemDao.getConnection()).destroyCreation(purchaseRequestId, user.getPerson().getId());

                pr.setId(null);
                PRCreationSocket.sendUpdatedPRCreation(user, pr, null);
                return;
            }

            updatePurchaseRequestData(user, purchaseRequestId, PurchaseItemDao, Helper.getBaseUrl(request));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void updatePurchaseRequestData(User user, int purchaseRequestId, PurchaseItemDAO purchaseItemDao, String baseUrl) throws SQLException {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setId(purchaseRequestId);

        purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);
        ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());

        products.sort(Item::compareTo);
        purchaseRequest.setListProducts(products);
        purchaseRequest.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(purchaseItemDao.getConnection());
        purchaseRequestDao.updateTotalAmount(purchaseRequest);
        ArrayList<Seller> sellers = new SellerDAO(purchaseItemDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());

        purchaseRequest.setPropagationCount(sellers.size());

        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        PRCreationSocket.sendUpdatedPRCreation(user, null, baseUrl);
    }

    private void abortCreation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {

            String purchaseRequestId = request.getParameter("purchaseRequestId");
            User user = (User) request.getSession().getAttribute("loggedUser");
            Person person = (Person) request.getSession().getAttribute("loggedPerson");

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setId(Integer.parseInt(purchaseRequestId));
            purchaseRequest.setBuyer(new Buyer(person.getId()));
            purchaseRequestDelete(user, purchaseRequest);

            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void purchaseRequestDelete(User user, PurchaseRequest purchaseRequest) throws SQLException {
        PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(true);
        PurchaseItemDao.initTransaction();

        PurchaseItemDao.removeAll(purchaseRequest.getId());
        new PurchaseRequestDAO(PurchaseItemDao.getConnection()).destroyCreation(purchaseRequest.getId(), purchaseRequest.getBuyer().getId());

        purchaseRequest.setId(null);
        PRCreationSocket.sendUpdatedPRCreation(user, purchaseRequest, null);
    }

    private void publish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            PurchaseRequest purchaseRequest = gson.fromJson(request.getParameter("purchaseRequest"), PurchaseRequest.class);

            if (purchaseRequest.validateDueDateInput()) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Data de expiração inválida", MessengerType.ERROR));
                return;
            }

            if ((new PurchaseRequestDAO(true).findById(new PurchaseRequest(purchaseRequest.getId()))).getPropagationCount() == 0) {
                response.setStatus(400);
                out = response.getWriter();
                Helper.responseMessage(out, new Messenger("Nenhum fornecedor encontrado para este pedido!", MessengerType.ERROR));
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
                person = new PersonDAO(true).findById(person);
                person.setUser(new UserDAO(true).findByPerson(person.getId()));

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
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
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

    private boolean missingAddressRegister(User user) {
        Address address = new AddressDAO(true).findByPerson(user.getPerson().getId());

        return address == null;
    }

    private void updateViewsCount(int productItemId, String baseUrl) {
        ProductItem productItem = new ProductItem();
        productItem.setId(productItemId);

        productItem = new ProductItemDAO(true).findById(productItem);
        productItem.setViewsCount(productItem.getViewsCount() + 1);
        new ProductItemDAO(true).updateViewsCount(productItem);

        new ElasticsearchService().updateProductItemViewsCount(productItemId, productItem.getViewsCount());
    }

    private void addProductItem(PrintWriter out, String baseUrl, User user, Item item) throws SQLException {
        Gson gJson = new Gson();

        Buyer buyer = new Buyer();
        buyer.setId(user.getPerson().getId());

        ProductItem productItem = (ProductItem) item.getProduct();
        productItem = new ProductItemDAO(true).findById(productItem);

        if (productItem == null) {
            Helper.responseMessage(out, new Messenger("Produto não encontrado.", MessengerType.WARNING));
            return;
        }
        item.setProduct(productItem);
        item.calculateAmount();

        PurchaseRequestDAO purchaseRequestDao = new PurchaseRequestDAO(true);
        purchaseRequestDao.initTransaction();
        PurchaseItemDAO PurchaseItemDao = new PurchaseItemDAO(purchaseRequestDao.getConnection());

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setBuyer(buyer);
        purchaseRequest.setStage(PRStage.CREATION);
        purchaseRequest.setQuotesVisibility(false);

        ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
        if (prs == null || prs.isEmpty()) {

            purchaseRequest.setViewsCount(0);
            purchaseRequest.setPropagationCount(0);
            purchaseRequest.addListProduct(item);
            purchaseRequest.calculateAmount();
            purchaseRequest.setDueDate(Calendar.getInstance());

            purchaseRequest = purchaseRequestDao.create(purchaseRequest);
        } else {
            purchaseRequest = prs.get(0);

            if (!new PurchaseItemDAO(true).validateProductInsertion(purchaseRequest.getId(), item)) {
                Helper.responseMessage(out, new Messenger("Produto já existente, altere sua quantidade ou remova-o.", MessengerType.WARNING));
                return;
            }
            ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
            products.add(item);
            products.sort(Item::compareTo);
            purchaseRequest.setListProducts(products);
            purchaseRequest.calculateAmount();

        }
        PurchaseItemDao.attachPurchaseRequest(purchaseRequest.getId(), item);

        ArrayList<Seller> sellers = new SellerDAO(PurchaseItemDao.getConnection()).findByPurchaseRequest(purchaseRequest.getId());
        purchaseRequest.setPropagationCount(sellers.size());
        purchaseRequestDao.updatePropagation(purchaseRequest);
        purchaseRequestDao.updateDueDate(purchaseRequest);

        purchaseRequestDao.closeTransaction();
        purchaseRequestDao.closeConnection();

        pushSocketPurchaseRequest(user, purchaseRequest, baseUrl);

        out.print(gJson.toJson(purchaseRequest));
        out.close();
    }

    private void pushSocketPurchaseRequest(User user, PurchaseRequest purchaseRequest, String baseUrl) {
        ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
        fetchPictures(products, baseUrl);

        purchaseRequest.setListProducts(products);

        user.setPerson(null);
        PRCreationSocket.sendUpdatedPRCreation(user, purchaseRequest, baseUrl);
    }

    private void fetchPictures(ArrayList<Item> products, String baseUrl) {
        products.forEach(item -> {
            synchronized (item) {
                ProductItem productItem = (ProductItem) item.getProduct();
                productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));
                productItem.setDefaultThumbnail(baseUrl);
            }
        });
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/purchase_request/", "");

        switch (action) {
            case "new":
                renderCreationPage(request, response);
                break;
            case "creation":
                getCreation(request, response);
                break;
            case "details":
                getDetails(request, response);
                break;
            case "suggest":
                suggest(request, response);
                break;
            default:
                list(request, response);
        }
    }

    private void renderCreationPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String purchaseRequestId = request.getParameter("pr");

            if (purchaseRequestId == null || purchaseRequestId.length() < 4) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setId(Integer.parseInt(purchaseRequestId));
            purchaseRequest = new PurchaseRequestDAO(true).findById(purchaseRequest);

            if (purchaseRequest == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            if (!purchaseRequest.getStage().equals(PRStage.CREATION)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-creation.jsp").forward(request, response);
        } catch (Exception err) {
            err.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private void getCreation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            Buyer buyer = new Buyer();
            buyer.setId(person.getId());

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setBuyer(buyer);
            purchaseRequest.setStage(PRStage.CREATION);

            ArrayList<PurchaseRequest> prs = new PurchaseRequestDAO(true).findByStageAndBuyer(purchaseRequest);
            if (prs != null && !prs.isEmpty()) {
                purchaseRequest = prs.get(0);

                ArrayList<Item> products = new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId());
                fetchPictures(products, Helper.getBaseUrl(request));

                products.sort(Item::compareTo);
                purchaseRequest.setListProducts(products);
                purchaseRequest.calculateAmount();
            } else {
                purchaseRequest = null;
            }

            out.print(gJson.toJson(purchaseRequest));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doGet [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void getDetails(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        String purchaseRequestIdString = request.getParameter("id");

        if (!isValidRequest(purchaseRequestIdString)) {
            response.sendRedirect("/");
            return;
        }

        PurchaseRequest pr = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));

        if (person.getId() != pr.getBuyer().getId()) {
            response.sendRedirect("/");
            return;
        }

        if (request.getHeader("Accept").contains("application/json")) {
            responseJsonDetails(request, response);
        } else {
            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-details.jsp").forward(request, response);
        }
    }

    private void responseJsonDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            PurchaseRequest purchaseRequest = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(request.getParameter("id"))));
            purchaseRequest.setQuotes(new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId()));
            purchaseRequest.setListProducts(new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId()));

            if (!PRStage.EXPIRED.equals(purchaseRequest.getStage()) && purchaseRequest.isExpired()) {
                new PurchaseRequestDAO(true).updateStage(purchaseRequest);
            }

            out.print(gson.toJson(purchaseRequest));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean isValidRequest(String purchaseRequestIdString) {
        boolean isValid = false;

        if (purchaseRequestIdString == null || purchaseRequestIdString.length() <= 4) {
            return isValid;
        }

        if (!Helper.isInteger(purchaseRequestIdString)) {
            return isValid;
        }

        PurchaseRequest pr = new PurchaseRequestDAO(true).findById(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));

        if (pr == null) {
            return isValid;
        }

        isValid = true;
        return isValid;
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getHeader("Accept").contains("application/json")) {
            responseListJson(request, response);
        } else {
            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-list.jsp").forward(request, response);
        }
    }

    private void responseListJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        try {
            ArrayList<PurchaseRequest> purchaseRequests = new PurchaseRequestDAO(true).findByBuyer(person.getId());
            purchaseRequests = purchaseRequests.stream()
                    .filter(purchaseRequest -> !PRStage.CREATION.equals(purchaseRequest.getStage()))
                    .peek(purchaseRequest -> {
                        purchaseRequest.setListProducts(new PurchaseItemDAO(true).findByPurchaseRequest(purchaseRequest.getId()));
                        fetchPictures(purchaseRequest.getListProducts(), Helper.getBaseUrl(request));
                        purchaseRequest.setQuotes(new QuoteDAO(true).findByPurchaseRequest(purchaseRequest.getId()));
                        if (!PRStage.EXPIRED.equals(purchaseRequest.getStage()) && purchaseRequest.isExpired()) {
                            new PurchaseRequestDAO(true).updateStage(purchaseRequest);
                        }
                    })
                    .sorted(Comparator.comparing(PurchaseRequest::getId))
                    .collect(Collectors.toCollection(ArrayList::new));

            out.print(gJson.toJson(purchaseRequests));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }


    private void suggest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getHeader("Accept").contains("application/json")) {
            responseSuggestJson(request, response);
        } else {
            renderSuggestPage(request, response);
        }
    }

    private void responseSuggestJson(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            Person person = (Person) request.getSession().getAttribute("loggedPerson");
            String purchaseRequestIdString = request.getParameter("pr");

            if (!isValidRequest(purchaseRequestIdString)) {
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
                    if (QuoteStatus.UNDER_REVIEW.equals(quote.getStatus()) && quote.isExpired()) {
                        new QuoteDAO(true).updateStatus(quote);
                    }
                });
                quotes.sort(Comparator.comparing(Quote::getCreatedAt));
                purchaseRequest.setQuotes(quotes);
            } else {
                ArrayList<Quote> restrictQuotes = new QuoteDAO(true).findRestrictQuotes(purchaseRequest.getId(), person.getId());
                restrictQuotes.forEach(this::populateQuote);
                restrictQuotes.sort(Comparator.comparing(Quote::getCreatedAt));
                purchaseRequest.setQuotes(restrictQuotes);
            }

            out.print(gJson.toJson(purchaseRequest));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void populateQuote(Quote quote) {
        quote.setCustomListProduct(new QuotationItemDAO(true).findByQuote(quote.getId()));
        quote.setShipmentOptions(new ShipmentDAO(true).findByQuoteAndSeller(quote.getId(), quote.getSeller().getId()));
    }

    private void renderSuggestPage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String purchaseRequestIdString = request.getParameter("pr");

        if (StringUtils.isBlank(purchaseRequestIdString)) {
            response.sendRedirect("/");
            return;
        }

        new PurchaseRequestDAO(true).updateViewsCount(new PurchaseRequest(Integer.parseInt(purchaseRequestIdString)));

        request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-suggest.jsp").forward(request, response);
    }
}
