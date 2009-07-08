<%@ page isELIgnored="false" %> 

Hello world!
${requestScope.names}
<%= getServletContext().getMajorVersion() %>
<%= getServletContext().getMinorVersion() %>
${pageContext.servletContext.majorVersion}