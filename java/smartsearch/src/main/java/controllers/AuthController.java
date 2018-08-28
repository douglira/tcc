package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import enums.Status;
import models.User;

@WebServlet(urlPatterns = "/auth")
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AuthController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		session.removeAttribute("loggedUser");
		response.sendRedirect("/smartsearch");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		User user = new User(request.getParameter("email"), request.getParameter("password"));
		User loggedUser = new UserDAO().authenticate(user);

		if (loggedUser == null) {
			request.setAttribute("error", "Email ou senha inválida");
			request.getRequestDispatcher("/signin").forward(request, response);
			return;
		}

		if (loggedUser.getStatus() == Status.INACTIVE) {
			request.setAttribute("error", "Cadastro desativado");
			request.getRequestDispatcher("/signin").forward(request, response);
			return;
		}

		HttpSession session = request.getSession();
		session.setAttribute("loggedUser", loggedUser);
		response.sendRedirect("/smartsearch");
	}

}
