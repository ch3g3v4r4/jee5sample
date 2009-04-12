package org.example;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.manager.EmployeeManager;
import org.example.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class HelloAppEngineServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloAppEngineServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		LOGGER.info("Say hello using sfl4j.");
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		EmployeeManager em = (EmployeeManager) context.getBean("employeeManagerImpl");
		Employee employee = new Employee() ;
		employee.setId(1L);
		employee.setFirstName("Thai");
		employee.setLastName("Ha");
		em.add(employee);
		resp.getWriter().println("Hello, world");
	}
}