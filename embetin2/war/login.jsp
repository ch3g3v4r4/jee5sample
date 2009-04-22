<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Teacher Desktop - Login</title>

<style type="text/css">
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dijit/themes/tundra/tundra.css";
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/resources/dojo.css";
</style>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/dojo.xd.js.uncompressed.js" djConfig="parseOnLoad: true, isDebug:false"></script>

<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.ValidationTextBox");
</script>
<style type="text/css">
body, html{
    margin:10px; padding:0; color:black;
    font-family: Verdana, Arial, san-serif; font-size:0.95em;
}
h1 { margin-top:1.0em; margin-bottom:0.6em; font-size:1.8em; color:#0066ff;}
h2 { margin-top:1.0em; margin-bottom:0.6em; font-size:1.6em; color:#0066ff;}
</style>

</head>
<body class="tundra">

<!-- heading -->
<h1>Teacher Desktop</h1>
<hr/>

<!-- Body -->
<h2>LOGIN</h2>
<form action="home.jsp">
<table border="0">
<tr><td><label for="username">Username:</label></td>
<td><input type="text" dojoType="dijit.form.ValidationTextBox" required="true" size="40" id="username"/></td></tr>
<tr><td><label for="password">Password:</label></td>
<td><input type="password" dojoType="dijit.form.ValidationTextBox" required="true" size="40" id="password"/></td></tr>
<tr><td colspan="2" align="center"><button dojoType="dijit.form.Button" type="submit">OK</button></td></tr>
</table>
</form>

<!-- Footer -->
<hr/>
<p align="center">Copyright &copy; 2009 Teacher Desktop</p>
</body>
</html>