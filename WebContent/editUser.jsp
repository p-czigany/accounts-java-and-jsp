<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    <h1>Edit User</h1>
        <table>
            <thead>
                <tr>
                    <th>Email</th>
                    <th>Time of Creation</th>
                    <th>Is Activated?</th>
                    <th>Is Deleted?</th>
                    <th>Password</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, Object> userData = (Map<String, Object>) result.getObject("userData");
                if (userData != null) {
                    String email = userData.get("email").toString();
                    String activated = userData.get("activated").toString();
                    String deleted = userData.get("deleted").toString();
                    String createDate = userData.get("createDate").toString();
                    String password = userData.get("password").toString();
                %>
                <tr>
                    <td><%= email %></td>
                    <td><%= createDate %></td>
                    <td><%= activated %></td>
                    <td><%= deleted %></td>
                    <td><%= password %></td>
                </tr>
                <%
                }
                %>
                <!-- <c:forEach var="entry" items="${userData}">
                    <tr>
                        <td>${entry.key}</td>
                        <td>${entry.value}</td>
                    </tr>
                </c:forEach> -->
            </tbody>
        </table>
</body>
</html>