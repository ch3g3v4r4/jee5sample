package org.example;

import java.io.IOException;
import javax.servlet.http.*;


public class HelloAppEngineServlet extends HttpServlet {

	//private static final Logger LOGGER = Logger.getLogger(HelloAppEngineServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		//LOGGER.info("Say hello.");
		resp.getWriter().println("Hello, world");
	}
}