package controllers.admin.command.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import models.Category;

public class NewCategory implements ICategoryCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		Category category = new Category();

		category.setTitle(request.getParameter("category-title"));
		category.setDescription(request.getParameter("category-description"));
		category.setLayer(1);

		String parentCategoryId = request.getParameter("category-id-selected");

		if (parentCategoryId != null && parentCategoryId.length() > 0) {
			Integer parentLayer = Integer.parseInt(request.getParameter("category-layer-selected"));

			Category parentCategory = new Category();
			parentCategory.setId(Integer.parseInt(parentCategoryId));

			category.setParent(parentCategory);

			category.setLayer(parentLayer + 1);
		}

		new CategoryDAO(true).create(category);
		try {
			response.sendRedirect("/admin/categories/new");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
