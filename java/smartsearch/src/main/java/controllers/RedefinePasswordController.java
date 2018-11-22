package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import libs.Helper;
import mail.MailSMTPService;
import mail.MailerService;
import mail.ResetPassword;
import models.User;

@WebServlet(name = "RedefinePasswordController", urlPatterns = {
		"/form/reset_pass",

		"/password/redefine/forgot",
		"/password/redefine/reset"
})
public class RedefinePasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RedefinePasswordController() {
		super();
	}

	private String getUrlRedirect(HttpServletRequest request, String token) {
		return Helper.getBaseUrl(request) + "/form/reset_pass?t=" + token;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String token = request.getParameter("t");
		if (token == null || token.length() <= 15) {
			response.sendRedirect(request.getContextPath() + "/form/forgot_pass");
			return;
		}

		request.setAttribute("token", token);
		request.getRequestDispatcher("/reset_pass.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String action = uri.replace("/password/redefine/", "");

		switch (action) {
			case "forgot":
				forgotPassRequest(request, response);
				break;
			case "reset":
				resetPassAction(request, response);
				break;
		}
	}

	private void forgotPassRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final User user = new UserDAO(true).checkIfExists(new User(request.getParameter("email")));

		if (user == null) {
			request.setAttribute("error", "E-mail inválido");
			request.getRequestDispatcher("/form/forgot_pass").forward(request, response);
			return;
		}

		try {
			user.processPassResetToken();
		} catch (Exception e) {
			System.out.println("FORGOT_PASS_ERROR: " + e);
			request.setAttribute("error", "Um erro inesperado aconteceu. Tente novamente mais tarde");
			request.getRequestDispatcher("/form/forgot_pass").forward(request, response);
			return;
		}

		final MailerService mailer = new ResetPassword(user.getDisplayName(),
				this.getUrlRedirect(request, user.getPasswordResetToken()));
		mailer.setMail(MailSMTPService.getInstance());
		mailer.setTo(user.getEmail());

		Runnable r = () -> {
			mailer.send();
			new UserDAO(true).setPasswordResetToken(user);
		};

		new Thread(r).start();

		response.sendRedirect("/");
	}
	
	private void resetPassAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		String token = request.getParameter("token");
		request.setAttribute("t", token);

		if (password == null || confirmPassword == null) {
			System.out.println("Campos inválidos");
			response.sendRedirect("/form/reset_pass?t=" + token);
//			request.getRequestDispatcher(request.getContextPath() + "/form/reset_pass?t=" + token).forward(request, response);
			return;
		}

		if (!password.equals(confirmPassword)) {
			System.out.println("Senhas nao batem");
			response.sendRedirect("/form/reset_pass?t=" + token);
//			request.getRequestDispatcher(request.getContextPath() + "/form/reset_pass?t=" + token).forward(request, response);
			return;
		}

		User user = new User();
		user.setPasswordResetToken(token);

		User userData = new UserDAO(true).findByPassResetToken(user);

		if (userData == null) {
			System.out.println("Token invalido");
			response.sendRedirect("/form/forgot_pass");
			return;
		}

		if (userData.isExpiredResetPassword()) {
			System.out.println("Expirou");
			response.sendRedirect("/form/forgot_pass");
			return;
		}

		userData.setPassword(password);
		userData.hashPassword();

		new UserDAO(true).updateResetPassword(userData);

		response.sendRedirect(request.getContextPath() + "/signin");

	}
}
