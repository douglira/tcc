package libs;

import com.google.gson.Gson;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import models.Messenger;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

public class Helper {
    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }

    public static void responseMessage(PrintWriter out, Messenger msg) {
        Gson gJson = new Gson();
        out.print(gJson.toJson(msg));
        out.close();
    }
    
    public static void responseMessage(PrintWriter out, Object obj) {
        Gson gJson = new Gson();
        out.print(gJson.toJson(obj));
        out.close();
    }

    public static boolean isInteger(String intString) {
        boolean isInteger = true;

        try {
            Integer.parseInt(intString, 10);
        } catch (NumberFormatException error) {
            isInteger = false;
        }

        return isInteger;
    }
}
