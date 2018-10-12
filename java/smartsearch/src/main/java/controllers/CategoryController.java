package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.CategoryDAO;
import models.Category;

@WebServlet(name = "CategoryController", urlPatterns = "/categories/json")
public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CategoryController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String parentId = request.getParameter("parentId");
		
		Gson gJson = new Gson();

		ArrayList<Category> categories = null;
		
		if (parentId != null && parentId.length() != 0) {
			categories = new CategoryDAO(true).subcategoriesByParent(Integer.parseInt(parentId));
			out.print(gJson.toJson(categories));
			out.close();
			return;
		}
		
		categories = new CategoryDAO(true).generals();
		out.print(gJson.toJson(categories));
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
