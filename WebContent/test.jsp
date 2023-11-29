<%@page import="java.net.URLDecoder"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" pageEncoding="UTF-8" session="false"%>
<%@ page import="com.xcite.core.servlet.ProcessResult"%>
<%	ProcessResult result = (ProcessResult)request.getAttribute("processResult");
	String content = result.getStringParameter("content"); %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>EXCITE - JAVA TEST</title>		
	</head>
	<body>
		<%=content %>
	</body>	
		
</html>