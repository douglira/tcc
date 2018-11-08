package controllers.user;

import com.google.gson.Gson;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.Person;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "PRListController", urlPatterns = {"/account/purchase_request/all"})
public class PRListController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        HttpSession session = request.getSession();
        Person person = (Person) session.getAttribute("loggedPerson");

        try {

        } catch (Exception error) {
            error.printStackTrace();
            System.out.println("PRSuggestController.doPost [ERROR]: " + error);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }
}
