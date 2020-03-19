<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Search Results</title>
</head>
<body>
<center><h1>List of Adoptable Animals</h1></center>

<div align="center">
	<h2>Results For: <i><%=request.getParameter("traits")%></i></h2>
    <table border="1" cellpadding="5">
        <tr>
        	<th>Name</th>
        	<th>Species</th>
            <th>Posted By</th>
            <th>Price</th>
            <th>Trait</th>
        </tr>
        <c:forEach var="traits" items="${listAnimals}">
        <tr>
        	<td>${animal.name}</td>
        	<td>${animal.species}</td>
        	<td>${animal.owner}</td>
        	<td>${animal.price}</td>
        	<td>${animal.trait}</td>
        </tr>
        </c:forEach>
        
</body>
</html>
