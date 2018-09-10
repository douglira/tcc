package controllers.admin.command.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ICategoryCommand {
	public void execute(HttpServletRequest request, HttpServletResponse response);
}
