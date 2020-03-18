<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>User Management Application</title>
</head>
<body>
	
    <div align="center">
       <h3 align="center"> User Table </h3>

	<table border="1" width="70%" align="center">

	<tr>

	<th>Username</th>
	
	<th>Password</th>
	
	<th>First Name</th>
	
	<th>Last Name</th>

	<th>Email</th>

	</tr>

	<c:forEach items="${listUsers}" var="users">

<tr>

<td>${users.username }</td>

<td>${users.password }</td>

<td>${users.firstname }</td>

<td>${users.lastname }</td>

<td>${users.email }</td>

</tr>

</c:forEach>

</table>
    </div>   
</body>
</html>