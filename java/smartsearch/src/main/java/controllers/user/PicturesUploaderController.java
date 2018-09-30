package controllers.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import libs.Helper;
import models.File;

@WebServlet("/products/pictures/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5
		* 5)
public class PicturesUploaderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIRECTORY = "pictures";

	public PicturesUploaderController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Gson gJson = new Gson();

		String uploadPath = request.getServletContext().getRealPath("") + java.io.File.separator + UPLOAD_DIRECTORY;
		java.io.File uploadDir = new java.io.File(uploadPath);

		if (!uploadDir.exists())
			uploadDir.mkdir();

		String baseUrl = Helper.getBaseUrl(request);

		ArrayList<File> pictures = new ArrayList<File>();

		for (Part part : request.getParts()) {
			String filename = String.valueOf(System.currentTimeMillis()) + "_" + part.getSubmittedFileName();
			String filenamePath = uploadPath + java.io.File.separator + filename;
			String urlPath = baseUrl + java.io.File.separator + UPLOAD_DIRECTORY + java.io.File.separator + filename;
			part.write(filenamePath);

			File picture = new File();
			picture.setName(part.getSubmittedFileName());
			picture.setFilename(filenamePath);
			picture.setUrlPath(urlPath);
			picture.setSize(part.getSize());

			String type = part.getContentType().toString().split("/")[0];
			String subtype = part.getContentType().toString().split("/")[1];

			picture.setType(type);
			picture.setSubtype(subtype);

			pictures.add(picture);
		}

		out.println(gJson.toJson(pictures));
		out.close();
	}
}
