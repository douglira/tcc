package controllers.user;

import com.google.gson.Gson;
import dao.FileDAO;
import dao.ProductDAO;
import dao.ProductItemDAO;
import enums.MessengerType;
import enums.ProductSituation;
import enums.Status;
import libs.Helper;
import models.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

@WebServlet(name = "RestrictProductController", urlPatterns = {
        "/account/products",

        "/account/products/new"
})
public class RestrictProductController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RestrictProductController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/products/", "");

        switch (action) {
            default:
                this.getSellerProducts(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/products/", "");

        switch (action) {
            case "new":
                createNewProduct(request, response);
                break;
        }
    }

    private void createNewProduct(HttpServletRequest request, HttpServletResponse response) throws IOException  {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        Messenger msg;

        try {

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            String categoryId = request.getParameter("categoryId");
            String productItemId = request.getParameter("productItemId");
            String productJson = request.getParameter("product");

            if (invalidParameters(categoryId, productJson)) {
                msg = new Messenger("Operação inválida.", MessengerType.ERROR);
                out.print(gJson.toJson(msg));
                return;
            }

            Product product = gJson.fromJson(productJson, Product.class);

            ProductItem productItem = new ProductItem();
            productItem.setTitle(product.getTitle().trim());
            productItem.setStatus(Status.ACTIVE);

            Seller seller = new Seller();
            seller.setId(person.getId());

            if (!validateTitle(seller, product.getTitle().trim())) {
                msg = new Messenger("Já existe um produto com este título em seu estoque", MessengerType.ERROR);
                out.print(gJson.toJson(msg));
                return;
            }

            Category category = new Category();
            category.setId(Integer.parseInt(categoryId));

            product.setCategory(category);
            product.setSeller(seller);
            product.setSituation(ProductSituation.LINKED);
            product.setStatus(Status.ACTIVE);
            product.setCreatedAt(Calendar.getInstance());

            ProductItemDAO productItemDao = new ProductItemDAO(true);

            boolean validateProductItemPictures = true;
            Integer remainingPicturesCount = ProductItem.MAX_PICTURES;

            if (productItemId == null || productItemId.length() == 0) {
                productItem = createNewProductItem(productItem, product, productItemDao);
                productItem.setDefaultThumbnail(Helper.getBaseUrl(request));

                msg = new Messenger("Seu novo produto foi cadastrado com sucesso em nosso sistema.", MessengerType.SUCCESS);
            } else {
                productItem.setId(Integer.parseInt(productItemId));
                attachToProductItem(product, productItem, productItemDao);
                remainingPicturesCount = fetchRemainingPictures(productItem);
                validateProductItemPictures = validateRemainingPictures(product, productItem, remainingPicturesCount);
                productItem.setDefaultThumbnail(Helper.getBaseUrl(request));

                msg = new Messenger("Seu novo produto foi vinculado a um anúncio já cadastrado com sucesso.",
                        MessengerType.SUCCESS);
            }

            product.setProductItem(productItem);
            ProductDAO productDao = new ProductDAO(productItemDao.getConnection());
            product = productDao.create(product);

            FileDAO fileDao = new FileDAO(productDao.getConnection());

            if (product.getPictures() != null && !product.getPictures().isEmpty()) {
                attachPictures(product, productItem, fileDao, validateProductItemPictures, remainingPicturesCount, Helper.getBaseUrl(request));
            }

            fileDao.closeTransaction();
            fileDao.closeConnection();

//            new ElasticsearchFacade().indexProductItem(productItem);

            out.print(gJson.toJson(msg));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("RestrictProductController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void attachPictures(Product product, ProductItem productItem, FileDAO fileDao, boolean validateProductItemPictures, int remainingPicturesCount, String baseUrl) {
        product.getPictures().forEach(picture -> {
            fileDao.create(picture);
            fileDao.attachProduct(product.getId(), picture.getId());
        });

        if (validateProductItemPictures) {
            for (File picture : product.getPictures()) {
                if (remainingPicturesCount == 0) {
                    break;
                }

                fileDao.attachProductItem(productItem.getId(), picture.getId());
                productItem.addPicture(picture);

                remainingPicturesCount--;
            }
        }
        productItem.setDefaultThumbnail(baseUrl);
    }

    private Integer fetchRemainingPictures(ProductItem productItem) {
        productItem.setPictures(new FileDAO(true).getProductItemPictures(productItem.getId()));

        return ProductItem.MAX_PICTURES - productItem.getPictures().size();
    }

    private Boolean validateRemainingPictures(Product product, ProductItem productItem, Integer remainingPicturesCount) {
        remainingPicturesCount = ProductItem.MAX_PICTURES - productItem.getPictures().size();
        return product.getPictures() != null && remainingPicturesCount > 0;
    }

    private void attachToProductItem(Product product, ProductItem productItem, ProductItemDAO productItemDao) throws SQLException {

        productItem = new ProductItemDAO(true).findById(productItem);
        productItem.setRelevance(productItem.getRelevance() + 1);
        productItem.setBasedProducts(new ProductDAO(true).findByProductItem(productItem.getId()));
        productItem.addBasedProduct(product);

        productItem.updatePrices();

        productItemDao.initTransaction();
        productItemDao.updatePricesAndRelevance(productItem);
    }

    private ProductItem createNewProductItem(ProductItem productItem, Product product, ProductItemDAO productItemDao) throws SQLException {
        productItem.setBasePrice(product.getBasePrice());
        productItem.setMaxPrice(product.getBasePrice());
        productItem.setMinPrice(product.getBasePrice());
        productItem.setCreatedAt(Calendar.getInstance());
        productItem.setPictures(new ArrayList<File>());

        productItemDao.initTransaction();
        return productItemDao.create(productItem);
    }

    private boolean invalidParameters(String categoryId, String productJson) {
        return categoryId == null || categoryId.length() == 0 || productJson == null || productJson.length() == 0;
    }

    private boolean validateTitle(Seller seller, String title) {
        boolean isValid = true;

        ArrayList<Product> products = new ProductDAO(true).findBySeller(seller.getId());
        if (!products.isEmpty()) {
            for (Product product : products) {
                if (product.getTitle().toLowerCase().trim().equals(title.toLowerCase().trim())) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    private void getSellerProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            String page = request.getParameter("page");
            String perPage = request.getParameter("perPage");
            String searchTitle = request.getParameter("search");

            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            String baseUrl = Helper.getBaseUrl(request);
            ArrayList<Product> products = null;

            if (searchTitle != null && searchTitle.length() > 2) {
                products = new ProductDAO(true)
                        .searchByTitle(searchTitle, person.getId());
            } else if (validatePagination(page, perPage)) {
                products = new ProductDAO(true)
                        .pagination(Integer.parseInt(page, 10), Integer.parseInt(perPage, 10), person.getId());
            } else {
                products = new ProductDAO(true)
                        .pagination(1, 15, person.getId());
            }

            fetchProductPictures(products, baseUrl);

            out.print(gson.toJson(products));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("RestrictProductController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void fetchProductPictures(ArrayList<Product> products, String baseUrl) {
        products.forEach(product -> {
            product.setPictures(new FileDAO(true).getProductPictures(product.getId()));
            product.setDefaultThumbnail(baseUrl);
        });
    }

    private boolean validatePagination(String page, String perPage) {
        boolean validation = true;

        if (page == null || perPage == null) {
            validation = false;
            return validation;
        }

        try {
            Integer.parseInt(page);
            Integer.parseInt(perPage);

        } catch (NumberFormatException err) {
            validation = false;
        }

        return validation;
    }

//    private String getSellerProductItems(Person person) {
//        Gson gJson = new Gson();
//
//        ArrayList<ProductItem> productItems = new  ProductItemDAO(true).findBySeller(person.getId());
//    }
}
