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
	<h1>Review an Animal</h1>
	<nav>
		<ul>
			<li><a href="index.jsp">Home</a></li>
			<li><a href="UsersForm.jsp">Edit User Information</a></li>
			<li><a href="index.jsp">[ !! UPDATE LINK !!] My Account</a></li>
		</ul>
	</nav>
	<form action="ReviewAn" method="post">
		<table border="1" cellpadding="5">
			<caption>
				<h2>Animal Review</h2>
			</caption>
			<tr>
				<th>Animal Rating</th>
				<td>
					<select size="1" name="rating" id="ratingDropMenu">
						<option>Select a Rating:</option>
						<option value="Totes Adorbs">Totes Adorbs</option>
						<option value="Adorbs">Adorbs</option>
						<option value="Cray">Cray</option>
						<option value="Cray-Cray">Cray-Cray</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>Comments:</th>
				<td>
					<!-- [ SCROLLING TEXTAREA REFERENCE: Web Dev p.408+ ] -->
					<!-- [ Max-length must match "comments" Attribute in the Reviews Table: Web Dev p.408+ ] -->
					<textarea id="comment" name="comment" cols="35" rows="4" maxlength="140" wrap="soft"></textarea>
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