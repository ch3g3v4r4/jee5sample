<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Tin Tin Application</title>

<style type="text/css">
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dijit/themes/tundra/tundra.css";
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/resources/dojo.css";
</style>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/dojo.xd.js.uncompressed.js" djConfig="parseOnLoad: true, isDebug:true"></script>

<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");

</script>
</head>
<body class="tundra">

<h1>Friends Talk</h1>

<div id="register">
<label for="name">Your name:</label> <input type="text" dojoType="dijit.form.TextBox" size="40" id="name"/>
<button dojoType="dijit.form.Button" id="myButton">
Join!
<script type="dojo/method" event="onClick">
console.log("hello world");
alert('You pressed the button');
</script>
</button>
</div>

</body>
</html>