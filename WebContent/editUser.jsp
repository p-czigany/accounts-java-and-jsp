<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
	<title>EXCITE - JAVA TEST</title>
</head>
<body>
    <h1>Edit User</h1>
    <form action="updateUser" method="POST">
        <c:forEach var="field" items="${userFields}">
            <c:if test="${field.key ne 'id'}">
                <label>${field.key}:</label>
                <input type="text" name="${field.key}" value="${field.value}" /><br/>
            </c:if>
        </c:forEach>
        <input type="submit" value="Save" />
    </form>
</body>
</html>