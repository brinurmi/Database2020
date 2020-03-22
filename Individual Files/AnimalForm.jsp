<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<style type="text/css">
		#darktheme {
			background-color: #5c5c5c;
			color: #ffffff;
		}
	</style>
	<title>Add Animal</title>
</head>
<body>

<div id="wrapper">
	<h1>Put an Animal Up for Adoption</h1>
	<nav>
		<ul>
			<li><a href="index.jsp">Home</a></li>
			<li><a href="UsersForm.jsp">Edit User Information</a></li>
			<li><a href="index.jsp">PLACEHOLDER</a></li>
		</ul>
	</nav>
	<form action="AddAnimalForAdoption" method="post">
		<table border="1" cellpadding="5">
			<caption>
				<h2>Animal Information</h2>
			</caption>
			<tr>
				<th>Name:</th>
				<td>
					<input type="text" name="name" size="45"/>
				</td>
			</tr>
			<tr>
				<th>Species:</th>
				<td>
					<input type="text" name="species" size="45"
						   value="<c:out value='${animal.species}' />"
					/>
				</td>
			</tr>
			<tr>
				<!-- [ DATE FORM REFERENCE: Web Dev. p.428] -->
				<!-- NOTE: Date selection does not work w/ Eclipse Internal Browser -->
				<th>Birth Date:</th>
				<td>
					<input type="date" name="birthDate" size="45"
						   value="<c:out value='${animal.birthDate}' />"
					/>
				</td>
			</tr>
			<tr>
				<th>Adoption Price: $</th>
				<td>
					<input type="number" name="adoptionPrice" size="45"
						   value="<c:out value='${animal.adoptionPrice}' />"
					/>
				</td>
			</tr>
			<tr>
				<th>Traits:</th>
				<td>
					<!-- !! TODO: Handle scenarios where user entered anything other than A-Z and spaces -->
					<!-- Possible Solution: Add java code to check (similar to DynamicDropSearch.jsp -->
					<!-- [ SCROLLING TEXTAREA REFERENCE: Web Dev p.408+ ] -->
					<textarea id="traits" name="traits" cols="30" rows="4" wrap="soft"></textarea>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" value="Submit"/>
				</td>
			</tr>
		</table>
	</form>
</div>
</body>
</html>