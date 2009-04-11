package org.example;

import java.io.IOException;
import javax.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloAppEngineServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloAppEngineServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		LOGGER.info("Say hello using sfl4j.");
		resp.getWriter().println("Hello, world");
	}
}