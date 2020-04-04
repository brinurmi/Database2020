<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>User Application</title>
</head>
<body>

<!--------------------------------------->
<!--       FORMERLY: UserForm.jsp      -->
<!-- Added 's' to match UsersList.jsp  -->
<!--------------------------------------->

<!-- !! Can we just make a check from ControlServlet, then have two different forms?  -->

<div align="center">
	<!-- TODO: Edit page title according to User existence -->
	<h1>User Information Form</h1>
</div>

<div align="center">
	<c:if test="${user != null}">                        <!-- User logged in: Update -->
	<form action="UpdateUser" method="post">
		</c:if>
		<c:if test="${user == null}">
		<form action="InsertUser" method="post">        <!-- User NOT logged in: Insert -->
			</c:if>
			<table border="1" cellpadding="5">
				<caption>
					<h2>
						<c:if test="${user != null}">
							Edit User
						</c:if>
						<c:if test="${user == null}">
							Add A New User
						</c:if>
					</h2>
				</caption>
				<c:if test="${user != null}">
					<!-- CHECK: Changed from "id" to "username", that correct? -->
					<input type="hidden" name="username" value="<c:out value='${user.username}' />"/>
				</c:if>
				<tr>
					<th>Username:</th>
					<td>
						<input type="text" name="username" size="45"
							   value="<c:out value='${user.username}' />"
						/>
					</td>
				</tr>
				<tr>
					<th>Password:</th>
					<td>
						<input type="password" name="password" size="45"
							   value="<c:out value='${user.password}' />"
						/>
					</td>
				</tr>
				<tr>
					<th>First Name:</th>
					<td>
						<input type="text" name="firstName" size="45"
							   value="<c:out value='${user.firstName}' />"
						/>
					</td>
				</tr>
				<tr>
					<th>Last Name:</th>
					<td>
						<input type="text" name="lastName" size="45"
							   value="<c:out value='${user.lastName}' />"
						/>
					</td>
				</tr>
				<tr>
					<th>Email:</th>
					<td>
						<input type="email" name="email" size="45"
							   value="<c:out value='${user.email}' />"
						/>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="submit" value="Register"/>
					</td>
				</tr>
			</table>
		</form>
		<a href="index.jsp">Return to Home</a>
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