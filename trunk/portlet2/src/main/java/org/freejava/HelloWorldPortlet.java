package org.freejava;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class HelloWorldPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response)
			throws IOException {
		PrintWriter writer = response.getWriter();
		writer.write("do View: Hello World from me!");
		writer.close();
	}

}
