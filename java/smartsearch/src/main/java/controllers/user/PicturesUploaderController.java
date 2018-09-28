package controllers.user;

import java.io.File;
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

import models.ProductPicture;

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

		String uploadPath = request.getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
		File uploadDir = new File(uploadPath);

		if (!uploadDir.exists())
			uploadDir.mkdir();

		String baseUrl = getBaseUrl(request);

		ArrayList<ProductPicture> pictures = new ArrayList<ProductPicture>();

		for (Part part : request.getParts()) {
			String filename = String.valueOf(System.currentTimeMillis()) + "_" + part.getSubmittedFileName();
			String filenamePath = uploadPath + File.separator + filename;
			String urlPath = baseUrl + File.separator + UPLOAD_DIRECTORY + File.separator + filename;
			part.write(filenamePath);

			ProductPicture picture = new ProductPicture();
			picture.setName(part.getSubmittedFileName());
			picture.setFilename(filenamePath);
			picture.setUrlPath(urlPath);
			picture.setSize(part.getSize());
			picture.setType(part.getContentType().toString());

			pictures.add(picture);
		}

		out.println(gJson.toJson(pictures));
		out.close();
	}

	private String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme() + "://";
		String serverName = request.getServerName();
		String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
		String contextPath = request.getContextPath();
		return scheme + serverName + serverPort + contextPath;
	}
}
