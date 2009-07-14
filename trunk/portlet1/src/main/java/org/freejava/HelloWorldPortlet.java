package org.freejava;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class HelloWorldPortlet extends GenericPortlet {

	@Override
	public void init() throws PortletException {
		System.out.println("Portlet initialized.");
	}
	public void doView(RenderRequest request, RenderResponse response)
			throws IOException {
		PrintWriter writer = response.getWriter();
		String info = "Server Info: " + getPortletConfig().getPortletContext().getServerInfo();
		info += "<br>Major Version: " + getPortletConfig().getPortletContext().getMajorVersion();
		info += "<br>Minor Version: " + getPortletConfig().getPortletContext().getMinorVersion();
		info += "<br>Window ID: " + request.getWindowID();

		writer.write(info);
		writer.close();
	}

	@Override
	public void destroy() {
		System.out.println("Portlet destroyed.");
	}
}
