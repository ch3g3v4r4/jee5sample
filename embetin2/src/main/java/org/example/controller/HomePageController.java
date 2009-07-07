package org.example.controller;

import org.example.manager.EmployeeManager;
import org.example.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomePageController {

	private EmployeeManager employeeManager;

	@Autowired
	public HomePageController(EmployeeManager employeeManager) {
		this.employeeManager = employeeManager;
	}

	@RequestMapping("/home.htm")
	public ModelAndView index() {
		Employee employee = new Employee() ;
		employee.setFirstName("Thai");
		employee.setLastName("Ha");
		employeeManager.add(employee);

		String names = "";
		for (Employee e : employeeManager.getAll()) {
			names += e.getFirstName();
		}

		return new ModelAndView("home.jsp", "names", names);
	}
}
