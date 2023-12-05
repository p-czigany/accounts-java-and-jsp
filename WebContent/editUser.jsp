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
    <form action="saveUser" method="post">
        <table>
            <thead>
                <tr>
                    <th>Email</th>
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
                    String password = userData.get("password").toString();
                %>
                <tr>
                    <td><input type="text" name="email" value="<%= email %>"></td>
                    <td><input type="checkbox" name="activated" <%= activated.equals("true") ? "checked" : "" %>></td>
                    <td><input type="checkbox" name="deleted" <%= deleted.equals("true") ? "checked" : "" %>></td>
                    <td><input type="password" name="password" value="<%= password %>"></td>
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
    <input type="submit" value="Save">
    </form>
</body>
</html>