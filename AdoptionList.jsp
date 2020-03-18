<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>List of All Animals Available for Adoption</title>
</head>
<body>
	<center>
	<h1>Available Adoptions</h1>
	<h2>
	
	<a href="new">Add New Animal</a>
	&nbsp;&nbsp;&nbsp;
	<a href="list">List All Animals</a>
	
	</h2>
	</center>
	
	<div align="center">
    <table border="1" cellpadding="5">
        <caption><h2>List of Current Animals</h2></caption>
        <tr>
        	<th>Name</th>
        	<th>Species</th>
            <th>Posted By</th>
            <th>Price</th>
        </tr>
        <c:forEach var="animal" items="${listAnimals}">
            <tr>
            	<td><c:out value="${animal.name}"/></td>
                <td><c:out value="${animal.species}"/></td>
                <td><c:out value="${animal.owner}"/></td>
                <td><c:out value="${animal.adoptionPrice}"/></td>
               
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
