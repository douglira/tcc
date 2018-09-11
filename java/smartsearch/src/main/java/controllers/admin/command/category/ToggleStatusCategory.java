package controllers.admin.command.category;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import enums.Status;
import models.Category;

public class ToggleStatusCategory implements ICategoryCommand {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			Category category = new Category();
			category.setId(Integer.parseInt(request.getParameter("category-id")));
			category.setStatus(Status.valueOf(request.getParameter("category-status")));
			category.toggleStatus();

			if (category.getStatus().equals(Status.ACTIVE)) {

				Category parentCategory = new CategoryDAO(true).findParentByChildId(category);

				if (parentCategory != null && parentCategory.getStatus().equals(Status.INACTIVE)) {
					
					out.println("{\"error\": \"Para ativar esta categoria é necessário ativar o grupo na qual pertence.\"}");
					return;
				}
			}
			
			new CategoryDAO(true).updateStatus(category);
			out.println("{\"error\": null}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
