package controllers;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import mail.IMailerService;
import mail.MailSMTPService;
import mail.MailerService;
import models.User;

@WebServlet(urlPatterns = { "/password/redefine", "/form/reset_pass" })
public class RedefinePassController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RedefinePassController() {
		super();
	}
	
	private String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme() + "://";
		String serverName = request.getServerName();
		String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		String contextPath = request.getContextPath();
		return scheme + serverName + serverPort + contextPath;
	}

	private String getUrlRedirect(HttpServletRequest request, String token) {
		return this.getBaseUrl(request) + "/form/reset_pass?t=" + token;
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
		String action = request.getParameter("action");

		if (action.equals("forgot_pass")) {
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

			final MailerService mailer = new MailerService(MailSMTPService.getInstance());
			mailer.setTo(user.getEmail());
			String displayName = user.getDisplayName();
			String urlRedirect = this.getUrlRedirect(request, user.getPasswordResetToken());

			Runnable r = new Runnable() {
				public void run() {
					mailer.sendResetPass(displayName, urlRedirect);
					new UserDAO(true).setPasswordResetToken(user);
				}
			};

			new Thread(r).start();

			response.sendRedirect("/");
		} else if (action.equals("reset_pass")) {
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String token = request.getParameter("token");
			request.setAttribute("t", token);

			if (password == null || confirmPassword == null) {
				System.out.println("Campos inválidos");
				response.sendRedirect("/form/reset_pass?t=" + token);
//				request.getRequestDispatcher(request.getContextPath() + "/form/reset_pass?t=" + token).forward(request, response);
				return;
			}

			if (!password.equals(confirmPassword)) {
				System.out.println("Senhas nao batem");
				response.sendRedirect("/form/reset_pass?t=" + token);
//				request.getRequestDispatcher(request.getContextPath() + "/form/reset_pass?t=" + token).forward(request, response);
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
}
