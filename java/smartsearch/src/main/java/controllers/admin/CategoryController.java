package controllers.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controllers.admin.command.category.ICategoryCommand;
import dao.CategoryDAO;
import models.Category;

@WebServlet(urlPatterns = { "/admin/categories/new", "/admin/categories/edit" })
public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String title = request.getParameter("title");

		if (title != null && title.length() > 0) {
			Category category = new Category();
			category.setTitle(title);

			category = new CategoryDAO(true).findByTitle(category);

			if (category == null) {
				request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp")
						.forward(request, response);
				return;
			}

			request.setAttribute("category", category);
			request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-edit.jsp").forward(request,
					response);
			return;
		}

		request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp").forward(request,
				response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		String className = "controllers.admin.command.category." + action + "Category";

		try {
			Class<?> classCommand = Class.forName(className);
			ICategoryCommand command = (ICategoryCommand) classCommand.newInstance();

			command.execute(request, response);
			response.sendRedirect("/admin/categories/new");
		} catch (Exception e) {
			System.out.println("ERROR_COMMAND_CATEGORY: " + e);
			response.sendRedirect("/admin/categories/new");
		}
	}

}
