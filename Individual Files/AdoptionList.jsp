<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<link rel="stylesheet" href="stylesheet.css">
	
	<title>Available Adoptions</title>
</head>
<body>
<div id="wrapper">
	<h1>List of All Animals Available for Adoption</h1>
	<nav>
		<ul>
			<li><a href="index.jsp">Home</a></li>
			<li><a href="UsersForm.jsp">Edit User Information</a></li>
			<li><a href="index.jsp">[ !! UPDATE LINK !!] My Account</a></li>
		</ul>
	</nav>
	
	<div align="center">
		<table border="1" cellpadding="5">
			<caption><h2>List of Current Animals</h2></caption>
			<tr>
				<th>Name</th>
				<th>Species</th>
				<th>Posted By</th>
				<th>Price</th>
				<th>Traits</th>
				<th></th>
			</tr>
			<c:forEach var="animal" items="${listAnimals}">
				<tr>
					<td><c:out value="${animal.name}"/></td>
					<td><c:out value="${animal.species}"/></td>
					<td><c:out value="${animal.ownerUsername}"/></td>
					<td><c:out value="${animal.adoptionPrice}"/></td>    <!-- TODO: Add output for traits -->
					<td>[ADD TRAITS OUTPUT METHOD]</td>
					<td>
						<!-- ID retrieval failing without .getID() -->
						<a href="ReviewForm.jsp?animalID=${animal.getID()}"/>
						Review this animal
						</a>
					</td>
				</tr>
			</c:forEach>
		</table>
		<h3>
			<a href="BeginPostAnimalProcess">Add New Animal</a>
			&nbsp;&nbsp;&nbsp;
		</h3>
	</div>
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