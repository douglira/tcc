package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;

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
import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import enums.Status;
import models.Category;
import models.Messenger;
import models.Person;
import models.Product;
import models.ProductItem;
import models.Seller;
import models.User;

@WebServlet(urlPatterns = { "/account/me/inventory" })
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

		product = validatePicturesPath(product);
		
		ProductItem productItem = new ProductItem();
		productItem.setTitle(product.getTitle());

		Person person = new Person();
		person.setUser(user);

		PersonDAO personDao = new PersonDAO(true);
		person = personDao.findByUser(person);

		Seller seller = new Seller();
		seller.setId(person.getId());

		Category category = new Category();
		category.setId(Integer.parseInt(categoryId));

		product.setCategory(category);
		product.setSeller(seller);
		product.setStatus(Status.ACTIVE);

		ProductItemDAO productItemDao = new ProductItemDAO(true);

		if (productItemId == null || productItemId.length() == 0) {

			productItem.setMarketPrice(product.getPrice());
			productItem.setMaxPrice(product.getPrice());
			productItem.setMinPrice(product.getPrice());

			productItemDao.initTransaction();
			productItem = productItemDao.create(productItem);

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

			msg = new Messenger("Seu novo produto foi vinculado a um anúncio já cadastrado com sucesso.",
					MessengerType.SUCCESS);
		}

		product.setProductItem(productItem);
		new ProductDAO(productItemDao.getConnection()).create(product);

		new ElasticsearchFacade().indexProductItem(productItem);
		out.print(gJson.toJson(msg));
		out.close();
	}

	private Product validatePicturesPath(Product product) {
		if (product.getPicturesPath() == null || product.getPicturesPath().size() == 0) {
			return product;
		}
		
		product.setTitle(product.getPicturesPath().get(0));
		return product;
	}
}
