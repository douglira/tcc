package controllers.user;

import com.google.gson.Gson;
import dao.FileDAO;
import dao.ProductDAO;
import dao.ProductItemDAO;
import database.elasticsearch.ElasticsearchFacade;
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
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/account/me/inventory"})
public class InventoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public InventoryController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        Messenger msg;

        try {
            String list = request.getParameter("list");
            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");
            String jsonElement = null;
            String baseUrl = Helper.getBaseUrl(request);

            if (list != null && list.equals("products")) {
                jsonElement = this.getSellerProducts(person, baseUrl);
            } else if (list != null && list.equals("product-items")) {
//                jsonElement = this.getSellerProductItems(person);
            } else {
                jsonElement = gJson.toJson(new Messenger("Ops, requisição inválida", MessengerType.ERROR));
            }

            out.print(jsonElement);
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("InventoryController.doGet [ERROR]: " + error);
            Helper.responseError(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

            if (categoryId == null || categoryId.length() == 0 || productJson == null || productJson.length() == 0) {
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

            ProductItemDAO productItemDao = new ProductItemDAO(true);

            ArrayList<File> pictures = new ArrayList<File>();
            File productThumbnail = new File();
            FileDAO fileDao = new FileDAO(false);

            boolean validateProductItemPictures;
            int remainingPicturesCount = ProductItem.MAX_PICTURES;
            if (productItemId == null || productItemId.length() == 0) {

                productItem.setMarketPrice(product.getPrice());
                productItem.setMaxPrice(product.getPrice());
                productItem.setMinPrice(product.getPrice());

                productItemDao.initTransaction();
                productItem = productItemDao.create(productItem);

                validateProductItemPictures = product.getPictures() != null && !product.getPictures().isEmpty();

                productThumbnail.setUrlPath(Helper.getBaseUrl(request) + "/assets/images/thumbnail-not-available.jpg");
                productThumbnail.setName("picture-not-available");
                productItem.setThumbnail(productThumbnail);

                msg = new Messenger("Seu novo produto foi cadastrado com sucesso em nosso sistema.", MessengerType.SUCCESS);
            } else {
                productItem.setId(Integer.parseInt(productItemId));

                productItem = productItemDao.findById(productItem);
                productItem.setRelevance(productItem.getRelevance() + 1);
                productItem.setBasedProducts(new ProductDAO(true).findByProductItem(productItem.getId()));
                productItem.addBasedProduct(product);

                productItem.updatePrices();

                productItemDao = new ProductItemDAO(true);
                productItemDao.initTransaction();
                productItemDao.updatePricesAndRelevance(productItem);

                fileDao = new FileDAO(productItemDao.getConnection());
                pictures = new FileDAO(true).getProductItemPictures(productItem.getId());

                remainingPicturesCount = ProductItem.MAX_PICTURES - pictures.size();
                validateProductItemPictures = product.getPictures() != null && remainingPicturesCount > 0;

                msg = new Messenger("Seu novo produto foi vinculado a um anúncio já cadastrado com sucesso.",
                        MessengerType.SUCCESS);
            }

            product.setProductItem(productItem);
            ProductDAO productDao = new ProductDAO(productItemDao.getConnection());
            product = productDao.create(product);
            fileDao.setConnection(productDao.getConnection());

            if (product.getPictures() != null && !product.getPictures().isEmpty()) {


                for (File picture : product.getPictures()) {
                    int index = product.getPictures().indexOf(picture);
                    picture = fileDao.create(picture);
                    fileDao.attachProduct(product.getId(), picture.getId());
                    product.getPictures().set(index, picture);
                }

                if (validateProductItemPictures) {
                    for (File picture : product.getPictures()) {
                        if (remainingPicturesCount == 0) {
                            break;
                        }

                        fileDao.attachProductItem(productItem.getId(), picture.getId());
                        pictures.add(picture);

                        remainingPicturesCount--;
                    }
                }
                productItem.setThumbnail(pictures.get(pictures.size() - 1));
                productItem.setPictures(pictures);
            }

            fileDao.closeTransaction();
            new ElasticsearchFacade().indexProductItem(productItem);
            out.print(gJson.toJson(msg));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("InventoryController.doPost [ERROR]: " + error);
            Helper.responseError(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
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

    private String getSellerProducts(Person person, String baseUrl) {
        Gson gJson = new Gson();

        ArrayList<Product> products = new ProductDAO(true).findBySeller(person.getId());

        products.forEach(product -> {
            product.setPictures(new FileDAO(true).getProductPictures(product.getId()));
            product.setDefaultThumbnail(baseUrl);
        });

        return gJson.toJson(products);
    }

//    private String getSellerProductItems(Person person) {
//        Gson gJson = new Gson();
//
//        ArrayList<ProductItem> productItems = new  ProductItemDAO(true).findBySeller(person.getId());
//    }
}
