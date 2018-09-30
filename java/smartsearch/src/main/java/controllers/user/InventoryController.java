package controllers.user;

import com.google.gson.Gson;
import dao.FileDAO;
import dao.PersonDAO;
import dao.ProductDAO;
import dao.ProductItemDAO;
import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import enums.ProductSituation;
import enums.Status;
import libs.Helper;
import models.*;

import javax.servlet.ServletException;
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
            throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Messenger msg;
        Gson gJson = new Gson();

        try {

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

            boolean validateProductItemPictures = false;
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
                productItem.setBasedProducts(new ProductDAO(true).findProductsByProductItem(productItem.getId()));
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
        } catch(Exception error) {
            error.printStackTrace();
            System.out.println("InventoryController.doPost [ERROR]: " + error);
            msg = new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR);
            out.print(gJson.toJson(msg));
            out.close();
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
}
