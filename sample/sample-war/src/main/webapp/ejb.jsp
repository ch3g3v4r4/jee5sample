<%@page import="javax.naming.InitialContext, com.example.sample.model.business.*, java.util.*"%>
NEW VERSION.
<%
InitialContext ctx = new InitialContext();
Sample sample = (Sample) ctx.lookup("java:comp/env/ejb/Sample");
%>
<%=sample.sayHello("Thai")%>
<%= sample.getUser(new Long(1)).getEmail() %>