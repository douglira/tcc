package controllers.user;

import com.google.gson.Gson;
import dao.ProductDAO;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.Person;
import models.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "ProductController", urlPatterns = "/account/products")
public class ProductController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            String page = request.getParameter("page");
            String perPage = request.getParameter("perPage");
            HttpSession session = request.getSession();
            Person person = (Person) session.getAttribute("loggedPerson");

            if (validatePagination(page, perPage)) {
                ArrayList<Product> products = new ProductDAO(true)
                        .pagination(Integer.parseInt(page, 10), Integer.parseInt(perPage, 10), person.getId());

                out.print(gson.toJson(products));
                out.close();
                return;
            }

            ArrayList<Product> products = new ProductDAO(true)
                    .pagination(1, 15, person.getId());

            out.print(gson.toJson(products));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("ProductController.doPost [ERROR]: " + err);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private boolean validatePagination(String page, String perPage) {
        boolean validation = true;

        if (page == null || perPage == null) {
            validation = false;
            return validation;
        }

        try {
            Integer.parseInt(page);
            Integer.parseInt(perPage);

        } catch (NumberFormatException err) {
            validation = false;
        }

        return validation;
    }
}
