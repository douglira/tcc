package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.elasticsearch.ElasticsearchFacade;
import enums.MessengerType;
import libs.Helper;
import models.Messenger;
import models.ProductItem;

@SuppressWarnings("serial")
@WebServlet(name = "ProductItemController", urlPatterns = {
        "/product_items/homepage",
        "/product_items/predict",
})
public class ProductItemController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/product_items/", "");

        switch (action) {
            case "homepage": {
                getHomepage(request, response);
            }
            case "predict": {
                getPrediction(request, response);
            }
        }
    }

    private void getHomepage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

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
            System.out.println("ProductItemController.doGet [ERROR]: " + e);
            Helper.responseMessage(out, new Messenger("Algo inesperado aconteceu, tente mais tarde.", MessengerType.ERROR));
        }
    }

    private void getPrediction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Gson gJson = new Gson();

        String productItemTitle = request.getParameter("productPredictTitle");

        List<ProductItem> products = new ElasticsearchFacade().getProductsItemPredict(productItemTitle);

        out.print(gJson.toJson(products));
        out.close();
    }
}
