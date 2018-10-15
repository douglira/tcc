package controllers.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PRSuggest", urlPatterns = "/account/purchase_request/suggest")
public class PRSuggest extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getHeader("Accept").contains("application/json")) {
            // Ajax response
        } else {
            request.getRequestDispatcher(request.getContextPath() + "/user/purchase-request-suggest.jsp").forward(request, response);
        }
    }
}
