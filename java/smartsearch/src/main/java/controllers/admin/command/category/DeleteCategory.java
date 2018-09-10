package controllers.admin.command.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import models.Category;

public class DeleteCategory implements ICategoryCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Category category = new Category();
		category.setId(Integer.parseInt(request.getParameter("category-id")));

		new CategoryDAO(true).destroy(category);
	}

}
