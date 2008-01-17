package com.example.sample.web;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.example.sample.model.business.Sample;
import com.example.sample.model.vo.User;

public class EJBTag extends SimpleTagSupport {

	@EJB private Sample sample;

	@Override
	public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        JspWriter out = pageContext.getOut();

        try {
            String sampleOutput = sample.sayHello("Loan");
            out.println(sampleOutput);
            out.println(sample.getUser(Long.valueOf(1)).getEmail());
            out.println("List:");
            for( User u : sample.getUsers()) {
            	out.print(u.getId() + ":" + u.getEmail());
            }
            out.println("<br>");
            sample.shopping();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
