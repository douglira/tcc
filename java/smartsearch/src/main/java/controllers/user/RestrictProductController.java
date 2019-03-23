package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import dao.FileDAO;
import dao.ProductDAO;
import dao.ProductItemDAO;
import enums.MessengerType;
import enums.ProductSituation;
import enums.Status;
import libs.Helper;
import models.Category;
import models.File;
import models.Messenger;
import models.Person;
import models.Product;
import models.ProductItem;
import models.Seller;
import services.elasticsearch.ElasticsearchService;

@WebServlet(name = "RestrictProductController", urlPatterns = {
        "/account/products",
        "/account/products/details",

        "/account/products/new",
        "/account/products/edit",
        "/account/products/delete"
})
public class RestrictProductController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RestrictProductController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/products", "");

        switch (action) {
        	case "/details": {
        		getDetails(request, response);
        		break;
        	}
            default:
                this.getSellerProducts(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account/products", "");

        switch (action) {
            case "/new": {
            	createNewProduct(request, response);
            	break;            	
            }
            case "/edit": {
            	editProduct(request, response);
            	break;
            }
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
            	response.setStatus(403);
            	Helper.responseMessage(response.getWriter(), new Messenger("Operação inválida", MessengerType.ERROR));
                return;
            }
            
            if (StringUtils.isBlank(categoryId)) {
            	response.setStatus(403);
            	Helper.responseMessage(response.getWriter(), new Messenger("Selecione uma categoria", MessengerType.ERROR));
            	return;
            }

            Product product = gJson.fromJson(productJson, Product.class);

            ProductItem productItem = new ProductItem();
            productItem.setTitle(product.getTitle().trim());
            productItem.setStatus(Status.ACTIVE);

            Seller seller = new Seller(person.getId());

            if (!validateTitle(seller, product.getTitle().trim())) {
                msg = new Messenger("Já existe um produto com este título em seu estoque", MessengerType.ERROR);
                out.print(gJson.toJson(msg));
                return;
            }

            Category category = new Category(Integer.parseInt(categoryId));

            product.setCategory(category);
            product.setSeller(seller);
            product.setSituation(ProductSituation.LINKED);
            product.setStatus(Status.ACTIVE);
            product.setCreatedAt(Calendar.getInstance());

            ProductItemDAO productItemDao = new ProductItemDAO(true);

            boolean validateProductItemPictures = true;
            Integer remainingPicturesCount = ProductItem.MAX_PICTURES;

            if (StringUtils.isBlank(productItemId)) {
                productItem = createNewProductItem(productItem, product, productItemDao);
                productItem.setDefaultThumbnail(Helper.getBaseUrl(request));

                msg = new Messenger("Seu novo produto foi cadastrado com sucesso em nosso sistema.", MessengerType.SUCCESS);
            } else {
                productItem.setId(Integer.parseInt(productItemId));
                attachToProductItem(product, productItem, productItemDao);
                remainingPicturesCount = fetchRemainingPicturesCount(productItem);
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

            new ElasticsearchService().indexProductItem(productItem);


            response.setStatus(201);
            out = response.getWriter();
            out.print(gJson.toJson(msg));
            out.close();
        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("RestrictProductController.createNewProduct [ERROR]: " + error);
            response.setStatus(403);
            Helper.responseMessage(response.getWriter(), new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }
    
    private void editProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	response.setContentType("text/html;charset=UTF-8");
    	
        try {
        	Product product;
        	
        	if (StringUtils.isNotBlank(request.getParameter("id"))) {
        		product = new Product(Integer.parseInt(request.getParameter("id")));
        	} else {
        		response.setStatus(403);
        		Helper.responseMessage(response.getWriter(), new Messenger("Operação inválida", MessengerType.ERROR));
        		return;
        	}
        	
        	new ProductDAO(true).findById(product);
        	
        	if (StringUtils.isNotBlank(request.getParameter("description"))) {
        		product.setDescription(request.getParameter("description"));
        	}
        	
        	if (StringUtils.isNotBlank(request.getParameter("availableQuantity")) && Integer.parseInt(request.getParameter("availableQuantity")) > 0) {
        		product.setAvailableQuantity(Integer.parseInt(request.getParameter("availableQuantity")));
        	}
        	
        	if (StringUtils.isNotBlank(request.getParameter("basePrice"))) {
        		product.setBasePrice(Double.parseDouble(request.getParameter("basePrice")));
        		
        		ProductItem productItem = new ProductItemDAO(true).findById(product.getProductItem());
                updateMarketPrices(productItem, product);
        		
        		ProductItemDAO productItemDao = new ProductItemDAO(true);
        		productItemDao.initTransaction();
        		productItemDao.updatePricesAndRelevance(productItem);
        		
        		ProductDAO productDao = new ProductDAO(productItemDao.getConnection());
        		productDao.update(product);
        		
        		new ElasticsearchService().updateProductItemPrices(productItem);
        	} else {
        		ProductDAO productDao = new ProductDAO(true);
        		productDao.initTransaction();
        		productDao.update(product);
        	}
        	
        	Helper.responseMessage(response.getWriter(), new Messenger("Produto atualizado com sucesso", MessengerType.SUCCESS));
        } catch (Exception error) {
        	error.printStackTrace();
            System.out.println("RestrictProductController.editProduct [ERROR]: " + error);
            response.setStatus(403);
            Helper.responseMessage(response.getWriter(), new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
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

    private Integer fetchRemainingPicturesCount(ProductItem productItem) {
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
        productItem.setCreatedAt(product.getCreatedAt());
        productItem.setPictures(new ArrayList<File>());

        productItemDao.initTransaction();
        return productItemDao.create(productItem);
    }

    private boolean invalidParameters(String categoryId, String productJson) {
        return StringUtils.isBlank(categoryId) || StringUtils.isBlank(productJson);
    }

    private boolean validateTitle(Seller seller, String title) {
        boolean isValid = true;

        ArrayList<Product> products = new ProductDAO(true).findBySeller(seller.getId());
        if (!products.isEmpty() && StringUtils.isNotBlank(title)) {
            for (Product product : products) {
                if (product.getTitle().toLowerCase().trim().equals(title.toLowerCase().trim())) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

    private void updateMarketPrices(ProductItem productItem, Product product) {
        productItem.setBasedProducts(
                new ProductDAO(true).findByProductItem(productItem.getId())
                        .stream()
                        .filter(productFilter -> !productFilter.getId().equals(product.getId()))
                        .collect(Collectors.toCollection(ArrayList::new)));
        productItem.addBasedProduct(product);
        productItem.updatePrices();
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

            if (StringUtils.isNotBlank(searchTitle)) {
                products = new ProductDAO(true).searchByTitle(searchTitle, person.getId());
            } else if (validatePagination(page, perPage)) {
                products = new ProductDAO(true)
                        .pagination(Integer.parseInt(page, 10), Integer.parseInt(perPage, 10), person.getId());
            } else {
                products = new ProductDAO(true).pagination(1, 15, person.getId());
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

        if (StringUtils.isBlank(page) || StringUtils.isBlank(perPage)) {
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

    private void getDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	if (request.getHeader("Accept").contains("application/json")) {
    		jsonGetDetails(request, response);
        } else {
            renderGetDetails(request, response);
        }   	
    }

	private void jsonGetDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		try {
			String productId = request.getParameter("id");
    		
	    	if (StringUtils.isBlank(productId)) {
	    		response.setStatus(403);
	            Helper.responseMessage(response.getWriter(), new Messenger("Operação inválida", MessengerType.ERROR, "INVALID_PARAMETER"));
	            return;
	        }
    	
    		Product product = new ProductDAO(true).findById(new Product(Integer.parseInt(productId)));

    		if (product == null) {
    			response.setStatus(403);
	            Helper.responseMessage(response.getWriter(), new Messenger("Operação inválida", MessengerType.ERROR, "INVALID_PARAMETER"));
	            return;
    		}
    		
    		product.setProductItem(new ProductItemDAO(true).findById(new ProductItem(product.getProductItem().getId())));
    		product.getProductItem().setPictures(new FileDAO(true).getProductItemPictures(product.getProductItem().getId()));
    		product.getProductItem().setDefaultThumbnail(Helper.getBaseUrl(request));
    		
    		product.setPictures(new FileDAO(true).getProductPictures(product.getId()));
    		product.setDefaultThumbnail(Helper.getBaseUrl(request));
    		
    		Helper.responseMessage(response.getWriter(), product);
    	} catch (Exception e) {
    		e.printStackTrace();
    		response.setStatus(403);
            Helper.responseMessage(response.getWriter(), new Messenger("Erro inesperado, tente novamente", MessengerType.ERROR));
    	}
	}

	private void renderGetDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String productId = request.getParameter("id");
			if (StringUtils.isBlank(productId)) {
				response.sendRedirect("/account/inventory");
	            return;
	        }
    	
    		Product product = new ProductDAO(true).findById(new Product(Integer.parseInt(productId)));

    		if (product == null) {
    			response.sendRedirect("/account/inventory");
	            return;
    		}
    		
    		request.getRequestDispatcher(request.getContextPath() + "/user/product-details.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("/account/inventory");
		}
	}
    
//    private String getSellerProductItems(Person person) {
//        Gson gJson = new Gson();
//
//        ArrayList<ProductItem> productItems = new  ProductItemDAO(true).findBySeller(person.getId());
//    }
}
