package controllers.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.MessengerType;
import libs.Helper;
import models.File;
import models.Messenger;
import models.User;
import services.S3Service;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name= "FilesController", urlPatterns = {
        "/files/upload/product_pictures",

        "/files/s3/upload/product_pictures",
        "/files/s3/delete/product_pictures",
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5
)
public class FilesController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_PICTURES_DIRECTORY = "pictures";
    private static final String BUCKET_KEY_PRODUCT_PICTURES = "product_pictures/";
    private S3Service s3Service;

    public FilesController() {
        super();
        this.s3Service = new S3Service();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String action = uri.replace("/files", "");

        switch (action) {
            case "/upload/product_pictures": {
                uploadServerProductPictures(request, response);
                break;
            }
            case "/s3/upload/product_pictures": {
                uploadS3ProductPictures(request, response);
                break;
            }
            case "/s3/delete/product_pictures": {
                deleteS3ProductPictures(request, response);
                break;
            }
        }
    }

    private void uploadS3ProductPictures(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        try {
            User user = (User) request.getSession().getAttribute("loggedUser");
            ArrayList<File> pictures = new ArrayList<File>();

            for (Part part : request.getParts()) {
                String filename = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
                String fileS3Key = BUCKET_KEY_PRODUCT_PICTURES + user.getUsername() + "/" + filename;
                String type = part.getContentType().split("/")[0];
                String subtype = part.getContentType().split("/")[1];

                String urlPath = this.s3Service.uploadObject(part.getInputStream(), part.getContentType(), fileS3Key);

                File picture = new File();
                picture.setName(part.getSubmittedFileName());
                picture.setFilePath(fileS3Key);
                picture.setUrlPath(urlPath);
                picture.setSize(part.getSize());
                picture.setType(type);
                picture.setSubtype(subtype);

                pictures.add(picture);
            }

            out.println(gJson.toJson(pictures));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RestrictPurchaseRequestController.doPost [ERROR]: " + e);
            response.setStatus(403);
            out = response.getWriter();
            Helper.responseMessage(out, new Messenger("Não foi possível salvar as imagens, tente novamente.", MessengerType.ERROR));
        }
    }

    private void deleteS3ProductPictures(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();
        try {
            ArrayList<File> pictures = gJson.fromJson(request.getParameter("pictures"), new TypeToken<ArrayList<File>>(){}.getType());

            for (File picture : pictures) {
                this.s3Service.deleteObject(picture.getFilePath());
            }
            Helper.responseMessage(out, new Messenger("Foto do produto excluída com sucesso", MessengerType.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(403);
            Helper.responseMessage(response.getWriter(), new Messenger("Não foi possível excluir a foto do produto", MessengerType.ERROR));
        }
    }

    private void uploadServerProductPictures(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gJson = new Gson();

        String uploadPath = request.getServletContext().getRealPath("") + UPLOAD_PICTURES_DIRECTORY;
        java.io.File uploadDir = new java.io.File(uploadPath);

        if (!uploadDir.exists()) uploadDir.mkdir();

        String baseUrl = Helper.getBaseUrl(request);

        ArrayList<File> pictures = new ArrayList<File>();

        for (Part part : request.getParts()) {
            String filename = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
            String filenamePath = uploadPath + java.io.File.separator + filename;
            String urlPath = baseUrl + java.io.File.separator + UPLOAD_PICTURES_DIRECTORY + java.io.File.separator + filename;
            String type = part.getContentType().split("/")[0];
            String subtype = part.getContentType().split("/")[1];
            part.write(filenamePath);

            File picture = new File();
            picture.setName(part.getSubmittedFileName());
            picture.setFilePath(filenamePath);
            picture.setUrlPath(urlPath);
            picture.setSize(part.getSize());
            picture.setType(type);
            picture.setSubtype(subtype);

            pictures.add(picture);
        }

        out.println(gJson.toJson(pictures));
        out.close();
    }
}
