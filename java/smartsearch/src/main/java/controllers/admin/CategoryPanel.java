package controllers.admin;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import models.Category;

@WebServlet(urlPatterns = { "/admin/categories" })
public class CategoryPanel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryPanel() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ArrayList<Category> categories = new CategoryDAO(true).generals();

		request.setAttribute("categories", categories);

		request.getRequestDispatcher(request.getContextPath() + "/admin/categories-panel.jsp").forward(request,
				response);
	}

//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//	}

}
