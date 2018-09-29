package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import dao.PersonDAO;
import dao.ProductDAO;
import dao.ProductItemDAO;
import dao.ProductPictureDAO;
import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import enums.ProductSituation;
import enums.Status;
import models.Category;
import models.Messenger;
import models.Person;
import models.Product;
import models.ProductItem;
import models.ProductPicture;
import models.Seller;
import models.User;

@WebServlet(urlPatterns = {"/account/me/inventory"})
public class InventoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public InventoryController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Messenger msg;
        Gson gJson = new Gson();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");

        String categoryId = request.getParameter("categoryId");
        String productItemId = request.getParameter("productItemId");
        String productJson = request.getParameter("product");

        if (categoryId == null || categoryId.length() == 0 || productJson == null || productJson.length() == 0) {
            msg = new Messenger("Operação inválida.", MessengerType.ERROR);
            out.print(gJson.toJson(msg));
            return;
        }

        Product product = gJson.fromJson(productJson, Product.class);

        product = validatePictures(product);

        ProductItem productItem = new ProductItem();
        productItem.setTitle(product.getTitle().trim());

        Person person = new Person();
        person.setUser(user);

        PersonDAO personDao = new PersonDAO(true);
        person = personDao.findByUser(person);

        Seller seller = new Seller();
        seller.setId(person.getId());

        if (!validateTitle(seller, product.getTitle().trim())) {
            msg = new Messenger("Já existe um produto com este título", MessengerType.ERROR);
            out.print(gJson.toJson(msg));
            return;
        }
        ;

        Category category = new Category();
        category.setId(Integer.parseInt(categoryId));

        product.setCategory(category);
        product.setSeller(seller);
        product.setSituation(ProductSituation.LINKED);
        product.setStatus(Status.ACTIVE);

        ProductItemDAO productItemDao = new ProductItemDAO(true);
        ProductPictureDAO pictureDao;
        ArrayList<ProductPicture> pictures = new ArrayList<ProductPicture>();

        boolean validatePictures = false;
        if (productItemId == null || productItemId.length() == 0) {

            productItem.setMarketPrice(product.getPrice());
            productItem.setMaxPrice(product.getPrice());
            productItem.setMinPrice(product.getPrice());

            productItemDao.initTransaction();
            productItem = productItemDao.create(productItem);

            pictureDao = new ProductPictureDAO(productItemDao.getConnection());
            validatePictures = product.getPictures() != null;

            ProductPicture picture = new ProductPicture();
            picture.setUrlPath(getBaseUrl(request) + "/assets/images/thumbnail-not-available.jpg");
            picture.setName("not-available");
            productItem.setThumbnail(picture);

            msg = new Messenger("Seu novo produto foi cadastrado com sucesso em nosso sistema.", MessengerType.SUCCESS);
        } else {
            productItem.setId(Integer.parseInt(productItemId));

            productItem = productItemDao.findById(productItem);
            productItem.setRelevance(productItem.getRelevance() + 1);
            productItem.setBasedProducts(new ProductDAO(true).findProductsByProductItem(productItem.getId()));
            productItem.addBasedProduct(product);

            productItem.updatePrices();

            productItemDao = new ProductItemDAO(true);
            productItemDao.initTransaction();
            productItemDao.updatePricesAndRelevance(productItem);

            pictureDao = new ProductPictureDAO(productItemDao.getConnection());
            pictures = new ProductPictureDAO(true).findByProductItem(productItem.getId());

            int remainingPicturesCount = ProductItem.MAX_PICTURES - pictures.size();
            validatePictures = product.getPictures() != null && remainingPicturesCount > 0;

            msg = new Messenger("Seu novo produto foi vinculado a um anúncio já cadastrado com sucesso.",
                    MessengerType.SUCCESS);
        }

        if (validatePictures) {
            for (ProductPicture picture : product.getPictures()) {
                picture.setProductItem(productItem);
                picture = pictureDao.create(picture);
                picture.setProductItem(null);
                pictures.add(picture);
            }

            productItem.setThumbnail(pictures.get(0));
            productItem.setPictures(pictures);
        }

        product.setProductItem(productItem);
        new ProductDAO(productItemDao.getConnection()).create(product);

        new ElasticsearchFacade().indexProductItem(productItem);
        out.print(gJson.toJson(msg));
        out.close();
    }

    private boolean validateTitle(Seller seller, String title) {
        boolean isValid = true;

        ArrayList<Product> products = new ProductDAO(true).findBySeller(seller.getId());
        if (!products.isEmpty()) {
            for (Product product : products) {
                if (product.getTitle().toLowerCase() == title.toLowerCase()) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    private Product validatePictures(Product product) {
        if (product.getPictures() == null || product.getPictures().isEmpty() || product.getPictures().size() == 0) {
            product.setPictures(null);
            return product;
        }

        product.setThumbnail(product.getPictures().get(0));
        return product;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }
}
