<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Teacher Desktop - PROGRESS REPORT</title>

<style type="text/css">
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dijit/themes/tundra/tundra.css";
@import "http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/resources/dojo.css";
</style>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.3.0/dojo/dojo.xd.js.uncompressed.js" djConfig="parseOnLoad: true, isDebug:false"></script>

<script type="text/javascript">
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.FilteringSelect");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dijit.form.Textarea");
dojo.require("dijit.Dialog");
dojo.require("dijit.Tooltip");

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
<h2>PROGRESS REPORT</h2>
<form action="progress.jsp">
<table border="0">
<tr>
	<td><label for="date">Date:</label></td>
	<td><input type="text" dojoType="dijit.form.DateTextBox" required="true" size="40" id="date"/></td>
</tr>
<tr>
    <td><label for="year">Year:</label></td>
    <td><select dojoType="dijit.form.FilteringSelect" name="year" autocomplete="false" value="7"><option value="7" selected="selected">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option></select></td>
</tr>
<tr>
    <td><label for="student">On:</label></td>
    <td><div dojoType="dojo.data.ItemFileReadStore" jsId="studentStore" url="students.txt"></div><input dojoType="dijit.form.FilteringSelect" store="studentStore" searchAttr="name" name="student" autocomplete="true"/></td>
</tr>
<tr>
    <td><label for="teacher">Teacher:</label></td>
    <td><div dojoType="dojo.data.ItemFileReadStore" jsId="teacherStore" url="teachers.txt"></div><input dojoType="dijit.form.FilteringSelect" store="teacherStore" searchAttr="name" name="teacher" autocomplete="true"/></td>
</tr>
<tr>
    <td><label for="reason">Reason for report:</label></td>
    <td><div dojoType="dojo.data.ItemFileReadStore" jsId="reasonStore" url="reasons.txt"></div><input dojoType="dijit.form.FilteringSelect" store="reasonStore" searchAttr="name" name="reason" autocomplete="true"/>
    <button id="addReasonButton" dojoType="dijit.form.Button">Add reason
        <script type="dojo/method" event="onClick" args="evt">dijit.byId("addReasonDialog").show();</script>
	</button>
	<div dojoType="dijit.Tooltip" style="display: none" connectId="addReasonButton">Admin Teachers can add more reason using this button</div>
    </td>
</tr>
<tr>
    <td><label for="class">Class:</label></td>
    <td><div dojoType="dojo.data.ItemFileReadStore" jsId="classStore" url="classes.txt"></div><input dojoType="dijit.form.FilteringSelect" store="classStore" searchAttr="name" name="class" autocomplete="true"/></td>
</tr>
<tr>
    <td><label for="attitude">Attitude about teacher:</label></td>
    <td><div dojoType="dojo.data.ItemFileReadStore" jsId="attitudeStore" url="attitudes.txt"></div><input dojoType="dijit.form.FilteringSelect" store="attitudeStore" searchAttr="name" name="attitude" autocomplete="true"/></td>
</tr>
<tr>
    <td><label for="comment">Comment:</label></td>
    <td>
        <textarea dojoType="dijit.form.Textarea" style="width:400px">

</textarea></td>
</tr>

<tr><td>&nbsp;</td><td><button dojoType="dijit.form.Button" iconClass="dijitEditorIcon dijitEditorIconSave" type="submit">OK</button></td></tr>

</table>
</form>

<div id="addReasonDialog" dojoType="dijit.Dialog" style="display: none" title="Add Reason">
    <label for="newReason">Reason:</label> <input type="text" dojoType="dijit.form.TextBox" size="40" id="newReason"/>
    <button dojoType="dijit.form.Button" iconClass="dijitEditorIcon dijitEditorIconSave" type="submit">OK
        <script type="dojo/method" event="onClick" args="evt">dijit.byId("addReasonDialog").hide();</script>
    </button>
</div>

<hr/>
<!-- Footer -->
<p align="center">Copyright &copy; 2009 Teacher Desktop</p>
</body>
</html>