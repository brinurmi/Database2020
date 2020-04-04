<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>User Management Application</title>
</head>
<body>
<center>
	<h1>My Account</h1>
	</h2>
</center>
<div align="center">
	<c:if test="${session == null}">
		response.sendRedirect("login.jsp");
	</c:if>
		<table border="1" cellpadding="5">
			<caption><h2>Current User Information</h2></caption>
			<tr>
				<th>Username</th>
				<c:if test="${sessionScope.sUsername == root}">		<!-- Hide password info unless root -->
					<th>Password</th>
				</c:if>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Email</th>
			</tr>
			<tr>
				<td><c:out value="${user.username}"/></td>
				
				<c:if test="${sessionScope.sUsername == root}">
					<td><c:out value="${user.password}"/></td>
				</c:if>
				
				<td><c:out value="${user.firstname}"/></td>
				<td><c:out value="${user.lastname}"/></td>
				<td><c:out value="${user.email}"/></td>
				<td>
					<a href="edit?id=<c:out value='${user.username}' />">Edit</a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="delete?id=<c:out value='${user.username}' />">Delete</a>
				</td>
			</tr>
		</table>
</div>
</body>
</html>


<!-- CSS here for now 🙃🙃🙃 -->
<style>
	/*-- Class Selectors --*/
	.text {
		text-align: left;
	}
	
	.resort {
		font-size: 1.2em;
		color: #000033;
	}
	
	
	/*--| Element Selectors |--*/
	
	table {
		margin: auto;
		border: 1px solid #3399cc;
		width: 90%;
		border-collapse: collapse;
	}
	
	td, th {
		border: 1px solid #3399cc;
		padding: 5px;
	}
	
	td {
		text-align: center;
	}
	
	tr:nth-of-type(even) {
		background-color: #f5fafc;
	}
	
	
	header, nav, main, footer {
		display: block; /* Ensures compatibility with older browsers */
	}
	
	header {
		background-color: #000033;
		color: #ffffff;
		font-family: Georgia, serif;
	}
	
	body {
		background-color: #ffffff;
		color: #666666;
		font-family: Verdana, Arial, sans-serif;
	}
	
	nav {
		font-weight: bold;
		float: left;
		width: 160px;
		padding: 20px 5px 0 20px;
	}
	
	nav a {
		text-decoration: none;
	}
	
	nav a:link {
		color: #000033;
	}
	
	nav a:visited {
		color: #344873;
	}
	
	nav a:hover {
		color: #ffffff;
	}
	
	nav ul {
		list-style-type: none; /* Removes list markers */
		margin: 0;
		padding-left: 0;
	}
	
	main {
		padding: 1px 1px 20px 20px;
		background-color: #ffffff;
		margin-left: 170px;
	}
	
	h1 {
		background-position: right;
		background-size: 100% 100%;
		background-repeat: no-repeat;
		color: #ffffff;
		padding-left: 20px;
		height: 72px;
		line-height: 200%;
		margin-bottom: 0;
	}
	
	h2 {
		color: #3399CC;
		font-family: Georgia, serif;
	}
	
	h3 {
		color: #000033;
	}
	
	dt {
		color: #000033;
		font-weight: bold;
	}
	
	footer {
		font-size: 0.70em;
		font-style: italic;
		padding: 10px;
		text-align: center;
		background-color: #ffffff;
		margin-left: 170px;
	}

</style>