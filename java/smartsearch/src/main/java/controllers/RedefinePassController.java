package controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDAO;
import models.User;

@WebServlet(urlPatterns = "/password/redefine")
public class RedefinePassController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RedefinePassController() {
		super();
	}

//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MessageDigest messageDigest = null;
		String action = request.getParameter("action");

		if (action.equals("forgot_pass")) {
			String email = request.getParameter("email");

			User user = new User();
			user.setEmail(email);

			user = new UserDAO().checkIfExists(user);

			if (user == null) {
				request.setAttribute("error", "E-mail inválido");
				request.getRequestDispatcher("/form/forgot_pass").forward(request, response);
				return;
			}

			try {
				messageDigest = MessageDigest.getInstance("MD5");

			} catch (Exception e) {
				System.out.println("FORGOT_PASS_ERROR[hash]: " + e);
				request.setAttribute("error", "Um erro inesperado aconteceu. Tente novamente mais tarde");
				request.getRequestDispatcher("/form/forgot_pass").forward(request, response);
				return;
			}
			messageDigest.update(email.getBytes(), 0, email.length());
			String token = ((String) new BigInteger(1, messageDigest.digest()).toString(16)).toUpperCase();

			Calendar expiresIn = Calendar.getInstance();
			expiresIn.add(Calendar.MINUTE, 10);
			
			user.setPasswordResetToken(token);
			user.setPasswordExpiresIn(expiresIn);

			new UserDAO().forgotPassRequest(user);
			
			response.sendRedirect(request.getContextPath());
		}
	}

}
