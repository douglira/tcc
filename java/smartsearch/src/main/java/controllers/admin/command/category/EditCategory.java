package controllers.admin.command.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import models.Category;

public class EditCategory implements ICategoryCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Category category = new Category();
		
		category.setId(Integer.parseInt(request.getParameter("category-id")));
		category.setTitle(request.getParameter("category-title"));
		category.setDescription(request.getParameter("category-description"));
		
		new CategoryDAO(true).saveDetails(category);
		try {
			response.sendRedirect("/admin/categories/new");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
