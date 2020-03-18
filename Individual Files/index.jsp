<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<title>Adoption Database</title>
	<!-- TODO: Fix CSS linking -->
	<link rel="stylesheet" href="Stylesheets/index.css">
	<style type="text/css">
		#darktheme{
			background-color: #5c5c5c;
			color: #FFFFFF;
		}
		
		#AnimalButton {
			margin: auto;
			padding: 30px;
		}
	</style>
</head>
<body id="darktheme">
<div id="wrapper">
	<header>
		<h1>Adoption Homepage</h1>
	</header>
	<nav>
		<ul>
			<li><a href="index.jsp">Home</a></li>
			<li><a href="UsersForm.jsp">Edit User Information</a></li>
			
			<!-- TODO: Decide whether to keep as button or link -->
			<!--<li><a action="AttemptAnimalPost" method="post">Put an Animal Up for Adoption</a></li>-->
			
			<li><a href="index.jsp">[PLACEHOLDER]</a></li>
		</ul>
	</nav>
	<main>
		<section>
			<aside>Floating Message Box (PLACEHOLDER)</aside>
		</section>
		<div id="AnimalButton">
			<form action="AttemptAnimalPost" method="post">
				<input type="submit" value="Put an Animal Up for Adoption"/>
			</form>
		</div>
	</main>
	<footer>
		<!-- Footer -->
	</footer>
</div>
</body>
</html>