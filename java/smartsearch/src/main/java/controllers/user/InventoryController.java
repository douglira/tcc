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
import enums.MessengerType;
import enums.Status;
import facades.ElasticsearchFacade;
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
		Gson gJson = new Gson();
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loggedUser");

		String categoryId = request.getParameter("categoryId");
		String productItemId = request.getParameter("productItemId");
		String productJson = request.getParameter("product");

		if (categoryId == null || categoryId.length() == 0 || productJson == null || productJson.length() == 0) {
			Messenger msg = new Messenger("Operação inválida.", MessengerType.ERROR);
			out.print(gJson.toJson(msg));
			return;
		}

		Product product = gJson.fromJson(productJson, Product.class);
		
		ProductItem productItem = new ProductItem();
		productItem.setTitle(product.getTitle());
		
		if (productItemId == null || productItemId.length() == 0) {
			Person person = new Person();
			person.setUser(user);
			
			PersonDAO personDao = new PersonDAO(true);
			person = personDao.findByUser(person);
			Seller seller = new Seller();
			seller.setId(person.getId());
			
			productItem.setMarketPrice(product.getPrice());
			productItem.setMaxPrice(product.getPrice());
			productItem.setMinPrice(product.getPrice());
			
			ProductItemDAO productItemDao = new ProductItemDAO(true);
			productItemDao.initTransaction();
			productItem = productItemDao.create(productItem);
			
			Category category = new Category();
			category.setId(Integer.parseInt(categoryId));
			
			product.setCategory(category);
			product.setSeller(seller);
			product.setProductItem(productItem);
			product.setStatus(Status.ACTIVE);
			
			new ProductDAO(productItemDao.getConnection(), false).create(product);
			
			ElasticsearchFacade elasticsearch = ElasticsearchFacade.getInstance();
			elasticsearch.indexProductItem(productItem);
			
			Messenger msg = new Messenger("Seu novo produto foi cadastrado com sucesso em nosso sistema.", MessengerType.SUCCESS);
			out.print(gJson.toJson(msg));
		} else {
			
			Messenger msg = new Messenger("Seu novo produto foi vinculado a um anúncio já cadastrado com sucesso.", MessengerType.SUCCESS);
			out.print(gJson.toJson(msg));			
		}
		
		out.close();
	}
}
