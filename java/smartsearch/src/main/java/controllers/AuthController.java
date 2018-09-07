package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.PersonDAO;
import dao.UserDAO;
import enums.Status;
import enums.UserRoles;
import models.Person;
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
		response.sendRedirect("/");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if (action.equals("signin")) {
			User user = new User(request.getParameter("email"));
			User loggedUser = new UserDAO(true).checkIfExists(user);

			if (loggedUser == null) {
				request.setAttribute("error", "Email ou senha inválida");
				request.getRequestDispatcher("/signin").forward(request, response);
				return;
			}
			
			if (!loggedUser.checkPassword(request.getParameter("password"))) {
				request.setAttribute("error", "Email ou senha inválida");
				request.getRequestDispatcher("/signin").forward(request, response);
				return;
			}

			if (loggedUser.getStatus() == Status.INACTIVE) {
				request.setAttribute("error", "Cadastro desativado");
				request.getRequestDispatcher("/signin").forward(request, response);
				return;
			}

			loggedUser.setPassword(null);
			HttpSession session = request.getSession();
			session.setAttribute("loggedUser", loggedUser);

			response.sendRedirect("/");
		} else if (action.equals("register")) {

			/**
			 * Extrai os dígitos da String e faz o parse pra Long
			 */
			Long tel = Long.parseLong(request.getParameter("tel").replaceAll("\\D+", ""));
			Long cnpj = Long.parseLong(request.getParameter("cnpj").replaceAll("\\D+", ""));

			Person person = new Person();
			person.setAccountOwner(request.getParameter("accountOwner"));
			person.setTel(tel);
			person.setCnpj(cnpj);
			person.setCorporateName(request.getParameter("corporateName"));
			person.setStateRegistration(Long.parseLong(request.getParameter("stateRegistration")));

			User user = new User();
			user.setEmail(request.getParameter("email"));
			user.setPassword(request.getParameter("password"));
			user.setUsername(request.getParameter("username"));
			user.setRole(UserRoles.COMMON);
			user.setStatus(Status.ACTIVE);

			user.generateDisplayName(person.getAccountOwner());
			user.hashPassword();

			UserDAO userDao = new UserDAO(true);
			userDao.setTransaction();
			user = userDao.create(user);
			
			person.setUser(user);
			
			PersonDAO personDao = new PersonDAO(userDao.getConnection(), false);
			personDao.create(person);
			
			response.sendRedirect(request.getContextPath() + "/signin");
		}
	}

}
