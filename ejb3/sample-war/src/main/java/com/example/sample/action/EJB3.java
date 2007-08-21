package com.example.sample.action;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspWriter;

import com.example.sample.model.business.Sample;
import com.example.sample.model.entities.Project;

public class EJB3 {

	public static Sample getBean() throws NamingException {
		InitialContext ctx = new InitialContext();
		Sample sample = (Sample) ctx.lookup("java:comp/env/ejb/Sample");
		return sample;
	}
	
	public static void run(JspWriter out) throws Exception {
		Sample s = getBean();
		Project p = new Project();
		p.setName("Project 1");
		p = s.createProject(p);
		out.print(p.getId());
	}
	
}
