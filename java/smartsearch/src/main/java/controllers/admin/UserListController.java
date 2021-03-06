package controllers.admin;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import models.User;

@WebServlet(name= "UserListController", urlPatterns = "/admin")
public class UserListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public UserListController() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String page = request.getParameter("page");
		String perPage = request.getParameter("perPage");
		
		page = page != null ? page : "1";
		perPage = perPage != null? perPage : "15";
		
		ArrayList<User> users = new UserDAO(true).report(Integer.parseInt(page), Integer.parseInt(perPage));
		
		
		request.setAttribute("users", users);
		
		request.getRequestDispatcher("/admin/home.jsp").forward(request, response);
	}

//    @Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doGet(request, response);
//	}

}
