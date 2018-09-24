package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import facade.ElasticsearchFacade;
import models.ProductItem;

@WebServlet(urlPatterns = "/products/search")
public class ProductSearchAjaxController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ProductSearchAjaxController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		Gson gJson = new Gson();

		String productItemTitle = request.getParameter("productPredictTitle");

		List<ProductItem> products = new ElasticsearchFacade().getProductsPredict(productItemTitle);

		out.print(gJson.toJson(products));
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
