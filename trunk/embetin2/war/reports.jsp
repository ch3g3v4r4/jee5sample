<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Teacher Desktop - Reports</title>

<style type="text/css">
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dijit/themes/tundra/tundra.css";
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/resources/dojo.css";
</style>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/dojo.xd.js.uncompressed.js" djConfig="parseOnLoad: true, isDebug:false"></script>

<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
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

<!-- Heading -->
<h1>Teacher Desktop</h1>

<!-- menu -->
<div id="sitelinks" style="font-size:0.8em;">
<p>&nbsp;&nbsp;<a href="home.jsp">HOME</a>&nbsp;|&nbsp;<a href="progress.jsp">PROGRESS REPORT</a>&nbsp;|&nbsp;<a href="referral_slip.jsp">STUDENT REFERRAL SLIP</a>&nbsp;|&nbsp;<a href="import.jsp">IMPORT</a>&nbsp;|&nbsp;<a href="reports.jsp">REPORTS</a>&nbsp;|&nbsp;<a href="login.jsp">LOGOUT</a></p>
</div>
<hr/>

<!-- Body -->
<h2>REPORTS</h2>
<p>Only admin teachers are allowed to use this function!</p>
<p>View reports. Filter and Export reports to Excel and PDF file formats.</p>
<label for="name">Your data:</label> <input type="text" dojoType="dijit.form.TextBox" size="40" id="name"/>

<center>
<button dojoType="dijit.form.Button" iconClass="dijitEditorIcon dijitEditorIconSave" type="submit">OK</button>
</center>

<hr/>
<!-- Footer -->
<p align="center">Copyright &copy; 2009 Teacher Desktop</p>
</body>
</html>