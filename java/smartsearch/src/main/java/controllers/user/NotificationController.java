package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.NotificationDAO;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.User;
import models.socket.Notification;

@SuppressWarnings("serial")
@WebServlet(name = "NotificationController", urlPatterns = "/account/me/notifications")
public class NotificationController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            User user = (User) request.getSession().getAttribute("loggedUser");
            ArrayList<Notification> notifications = new NotificationDAO(true).findLastOnes(user.getId());

            out.print(gson.toJson(notifications));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("NotificationController.doPost [ERROR]: " + err);
            response.setStatus(503);
            Helper.responseMessage(out, new Messenger("Não foi possível importar suas notificações.", MessengerType.ERROR));
        }
    }
}
