package controllers.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import models.Category;

@WebServlet(urlPatterns = "/admin/categories/new")
public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CategoryController() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp").forward(request, response);
	}

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    	
    	new CategoryDAO().create(category);
    	
    	response.sendRedirect("/admin/categories/new");
	}

}
