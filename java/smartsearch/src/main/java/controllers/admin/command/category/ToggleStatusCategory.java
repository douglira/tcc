package controllers.admin.command.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import enums.Status;
import models.Category;

public class ToggleStatusCategory implements ICategoryCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Category category = new Category();
		category.setId(Integer.parseInt(request.getParameter("category-id")));
		category.setStatus(Status.valueOf(request.getParameter("category-status")));
		category.toggleStatus();

		new CategoryDAO(true).updateStatus(category);
	}

}
