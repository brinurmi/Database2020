<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>User Management Application</title>
</head>
<body>
<center>
    <h1>User Management</h1>
    <h2>
        <a href="new">Add New User</a>
        &nbsp;&nbsp;&nbsp;
        <a href="list">List All User</a>

    </h2>
</center>
<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>List of Current Users</h2></caption>
        <tr>
        	<th>Username</th>
        	<th>Password</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email </th>
        </tr>
        <c:forEach var="users" items="${listUsers}">
            <tr>
            	<td><c:out value="${user.username}"/></td>
                <td><c:out value="${user.password}"/></td>
                <td><c:out value="${user.firstname}"/></td>
                <td><c:out value="${user.lastname}"/></td>
                <td><c:out value="${user.email}"/></td>
                <td>
                    <a href="edit?id=<c:out value='${user.username}' />">Edit</a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="delete?id=<c:out value='${user.username}' />">Delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>