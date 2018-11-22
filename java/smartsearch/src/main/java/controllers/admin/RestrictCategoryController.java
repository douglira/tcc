package controllers.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import dao.CategoryDAO;
import enums.MessengerType;
import enums.Status;
import libs.Helper;
import models.Category;
import models.Messenger;

@WebServlet(name = "RestrictCategoryController", urlPatterns = {
//        GET
        "/admin/categories",
        "/admin/categories/list",

//        POST
        "/admin/categories/new",
        "/admin/categories/edit",
        "/admin/categories/toggle_status",
        "/admin/categories/delete"
})
public class RestrictCategoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getHeader("Accept").contains("application/json")) {
            responseJsonList(request, response);
        } else {
            String uri = request.getRequestURI();
            String action = uri.replace("/admin/categories/", "");

            switch (action) {
                case "new":
                    categoriesFormNew(request, response);
                    break;
                case "edit":
                    categoriesFormEdit(request, response);
                    break;
                default:
                    categoriesPanel(request, response);
            }
        }
    }

    private void responseJsonList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String parentId = request.getParameter("parentId");

        Gson gJson = new Gson();

        ArrayList<Category> categories = null;

        if (parentId != null && parentId.length() != 0) {
            categories = new CategoryDAO(true).restrictSubcategoriesByParent(Integer.parseInt(parentId));
            out.print(gJson.toJson(categories));
            out.close();
            return;
        }

        categories = new CategoryDAO(true).restrictGenerals();
        out.print(gJson.toJson(categories));
        out.close();
    }

    private void categoriesFormNew(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp").forward(request, response);
    }

    private void categoriesFormEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");

        if (title != null && title.length() > 0) {
            Category category = new Category();
            category.setTitle(title);

            category = new CategoryDAO(true).findByTitle(category);

            if (category == null) {
                categoriesFormNew(request, response);
                return;
            }

            request.setAttribute("category", category);
            request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-edit.jsp").forward(request, response);
            return;
        }

        categoriesFormNew(request, response);
    }

    private void categoriesPanel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<Category> categories = new CategoryDAO(true).restrictGenerals();

        request.setAttribute("categories", categories);

        request.getRequestDispatcher(request.getContextPath() + "/admin/categories-panel.jsp").forward(request,
                response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/admin/categories/", "");

        try {
            switch (action) {
                case "new":
                    create(request, response);
                    break;
                case "edit":
                    edit(request, response);
                    break;
                case "toggle_status":
                    toggleStatus(request, response);
                    break;
                case "delete":
                    delete(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PublicCategoryController.doPost [ERROR]: " + e);
            response.sendRedirect("/admin/categories/new");
        }

    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Category category = new Category();

        category.setTitle(request.getParameter("category-title"));
        category.setDescription(request.getParameter("category-description"));
        category.setLayer(1);

        String parentCategoryId = request.getParameter("category-id-selected");

        if (parentCategoryId != null && parentCategoryId.length() > 0) {
            Integer parentLayer = Integer.parseInt(request.getParameter("category-layer-selected"));

            Category parentCategory = new Category();
            parentCategory.setId(Integer.parseInt(parentCategoryId));

            category.setParent(parentCategory);

            category.setLayer(parentLayer + 1);
        }

        new CategoryDAO(true).create(category);

        response.sendRedirect("/admin/categories/new");
    }

    private void edit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Category category = new Category();

        category.setId(Integer.parseInt(request.getParameter("category-id")));
        category.setTitle(request.getParameter("category-title"));
        category.setDescription(request.getParameter("category-description"));

        new CategoryDAO(true).saveDetails(category);

        response.sendRedirect("/admin/categories/new");
    }

    private void toggleStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Category category = new Category();
        category.setId(Integer.parseInt(request.getParameter("category-id")));
        category.setStatus(Status.valueOf(request.getParameter("category-status")));
        category.toggleStatus();

        if (category.getStatus().equals(Status.ACTIVE)) {

            Category parentCategory = new CategoryDAO(true).findParentByChildId(category);

            if (parentCategory != null && parentCategory.getStatus().equals(Status.INACTIVE)) {

                Helper.responseMessage(out, new Messenger("Para ativar esta categoria é necessário ativar o grupo na qual pertence.", MessengerType.ERROR));
                return;
            }
        }

        new CategoryDAO(true).updateStatus(category);
        Helper.responseMessage(out, new Messenger("Categoria atualizada com sucesso.", MessengerType.SUCCESS));
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Category category = new Category();
        category.setId(Integer.parseInt(request.getParameter("category-id")));

        new CategoryDAO(true).destroy(category);

        response.sendRedirect("/admin/categories/new");
    }
}
