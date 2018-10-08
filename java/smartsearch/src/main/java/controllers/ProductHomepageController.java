package controllers;

import com.google.gson.Gson;
import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.ProductItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "ProductHomepageController", urlPatterns = "/products/homepage")
public class ProductHomepageController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        Messenger msg;

        try {
            String page = request.getParameter("page");
            String perPage = request.getParameter("perPage");

            if (page == null) {
                page = "1";
            }

            if (perPage == null) {
                perPage = "15";
            }

            ElasticsearchFacade elasticsearchFacade = new ElasticsearchFacade();
            ArrayList<ProductItem> productItems = elasticsearchFacade
                    .getProductsItemHomepage(Integer.parseInt(page), Integer.parseInt(perPage));

            out.print(gJson.toJson(productItems));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ProductHomepageController.doGet [ERROR]: " + e);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }

    }
}
