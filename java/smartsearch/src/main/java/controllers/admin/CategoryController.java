package controllers.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CategoryDAO;
import enums.Status;
import models.Category;

@WebServlet(name = "CategoryController", urlPatterns = {"/admin/categories/new", "/admin/categories/edit"})
public class CategoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");

        if (title != null && title.length() > 0) {
            Category category = new Category();
            category.setTitle(title);

            category = new CategoryDAO(true).findByTitle(category);

            if (category == null) {
                request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("category", category);
            request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-edit.jsp").forward(request,
                    response);
            return;
        }

        request.getRequestDispatcher(request.getContextPath() + "/admin/categories-form-create.jsp").forward(request,
                response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "New":
                    create(request);
                    break;
                case "Edit":
                    edit(request);
                    break;
                case "ToggleStatus":
                    toggleStatus(request, response);
                    break;
                case "Delete":
                    delete(request);
                    break;
            }

            response.sendRedirect("/admin/categories/new");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CategoryController.doPost [ERROR]: " + e);
            response.sendRedirect("/admin/categories/new");
        }

    }

    private void create(HttpServletRequest request) {
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
    }

    private void edit(HttpServletRequest request) {
        Category category = new Category();

        category.setId(Integer.parseInt(request.getParameter("category-id")));
        category.setTitle(request.getParameter("category-title"));
        category.setDescription(request.getParameter("category-description"));

        new CategoryDAO(true).saveDetails(category);
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

                out.println("{\"error\": \"Para ativar esta categoria é necessário ativar o grupo na qual pertence.\"}");
                return;
            }
        }

        new CategoryDAO(true).updateStatus(category);
        out.println("{\"error\": null}");
    }

    private void delete(HttpServletRequest request) {
        Category category = new Category();
        category.setId(Integer.parseInt(request.getParameter("category-id")));

        new CategoryDAO(true).destroy(category);
    }
}
