package controllers.user;

import com.google.gson.Gson;
import dao.AddressDAO;
import enums.MessengerType;
import enums.ShipmentMethod;
import libs.Helper;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ShipmentController", urlPatterns = {
    "/shipments/available"
})
public class ShipmentController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/shipments", "");

        switch (action) {
            case "/available": {
                getAvailableOptions(request, response);
                break;
            }
        }
    }

    private void getAvailableOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            Person loggedPerson = (Person) request.getSession().getAttribute("loggedPerson");
            Address address = new AddressDAO(true).findByPerson(loggedPerson.getId());

            List<Shipment> availableShipmentOptions = Arrays.asList(
                    new Shipment(ShipmentMethod.CUSTOM),
                    new Shipment(ShipmentMethod.FREE)
            );

            if (address != null) {
              availableShipmentOptions.add(new Shipment(ShipmentMethod.LOCAL_PICK_UP));
            }

            out.print(gJson.toJson(availableShipmentOptions));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ShipmentController.getAvailableOptions [ERROR]: " + e);
            response.setStatus(403);
            out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Não foi possível carregar as opções de envio.", MessengerType.ERROR));
        }
    }
}
