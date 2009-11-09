/**
 * 
 */
package org.xwiki.webserver.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xwiki.appserver.TomcatPlugin;
import org.xwiki.appserver.WebServer;


/**
 * @author kotelnikov
 * 
 */
public class AdminServlet extends HttpServlet {

	/**
	 * 
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (path == null)
			path = "/";
		String command = path.substring(1);
		if (!execCommand(command, request, response))
			forward("/index.jsp", request, response);
	}

	private boolean execCommand(String command, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if ("stop".equals(command)) {
			WebServer server = TomcatPlugin.getDefault().getWebServer();
			include("/stopping.jsp", request, response);
			server.stop();
			return true;
		}
		return false;
	}

	protected void include(String url, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(url).include(request, response);
	}

	protected void forward(String url, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(url).forward(request, response);
	}

}
