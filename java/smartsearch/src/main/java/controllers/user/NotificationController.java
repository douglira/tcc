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
import dao.UserDAO;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.User;
import models.socket.Notification;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "NotificationController", urlPatterns = {
        "/account/me/notifications",
        "/account/me/notifications/pagination"
})
public class NotificationController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/account", "");

        switch (action) {
            case "/me/notifications": {
                lastNotifications(request, response);
                break;
            }
            case "/me/notifications/pagination": {
                paginateNotifications(request, response);
                break;
            }
        }
    }

    private void lastNotifications(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            System.out.println("NotificationController.lastNotifications [ERROR]: " + err);
            response.setStatus(503);
            Helper.responseMessage(out, new Messenger("Não foi possível importar suas notificações.", MessengerType.ERROR));
        }
    }

    private void paginateNotifications(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        try {
            String page = request.getParameter("page");
            String perPage = request.getParameter("perPage");

            if (StringUtils.isBlank(page) || !Helper.isInteger(page)) {
                page = "1";
            }

            if (StringUtils.isBlank(perPage) || !Helper.isInteger(perPage)) {
                perPage = "20";
            }

            User user = (User) request.getSession().getAttribute("loggedUser");
            ArrayList<Notification> notifications = new NotificationDAO(true)
                    .pagination(user.getId(), Integer.parseInt(page), Integer.parseInt(perPage));

            notifications.forEach(notification -> {
                if (notification.getFrom() != null) {
                    new UserDAO(true).findById(notification.getFrom());
                }
            });

            out.print(gson.toJson(notifications));
            out.close();
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("NotificationController.paginateNotifications [ERROR]: " + err);
            response.setStatus(503);
            Helper.responseMessage(out, new Messenger("Não foi possível importar suas notificações.", MessengerType.ERROR));
        }
    }
}
