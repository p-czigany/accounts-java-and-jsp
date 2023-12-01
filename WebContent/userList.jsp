<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" pageEncoding="UTF-8" session="false"%>
<%@ page import="com.xcite.core.servlet.ProcessResult"%>
<%	ProcessResult result = (ProcessResult)request.getAttribute("processResult");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>EXCITE - JAVA TEST</title>		
	</head>
	<body>
        <h1>User List</h1>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>Time of Creation</th>
                    <th>Is Activated?</th>
                    <th>Is Deleted?</th>
                    <th>Password</th>
                </tr>
            </thead>
            <tbody>
                <%

List<Map<String, Object>> userList = (List<Map<String, Object>>) result.getObject("userList");
                if (userList != null) {
                    for (Map<String, Object> user : userList) {
                        String id = user.get("id").toString();
                        String email = user.get("email").toString();
                        String activated = user.get("activated").toString();
                        String deleted = user.get("deleted").toString();
                        String createDate = user.get("createDate").toString();
                        String password = user.get("password").toString();
                %>
                <tr>
                    <td><%= id %></td>
                    <td><%= email %></td>
                    <td><%= createDate %></td>
                    <td><%= activated %></td>
                    <td><%= deleted %></td>
                    <td><%= password %></td>
                </tr>
                <%
                    }
                }
                %>
            </tbody>
        </table>
    </body>
		
</html>