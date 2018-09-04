package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import enums.UserRoles;
import models.User;

public class AdminFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpSession session = ((HttpServletRequest) request).getSession();
		User user = (User) session.getAttribute("loggedUser");
		
		if (user != null && user.getRole().equals(UserRoles.ADMIN)) {
			chain.doFilter(request, response);
		} else {
			((HttpServletResponse) response).sendRedirect("/");
		}
	}

	@Override
	public void destroy() {
		
	}

	
}
