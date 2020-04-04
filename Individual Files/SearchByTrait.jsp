<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>

<!------------------------------------------------->
<!-- !! !! FORMERLY: DynamicDropSearch.jsp !! !! -->
<!------------------------------------------------->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Search by Trait</title>
</head>
<body>
<h1>Search For Adoptable Animals</h1>
<p>Please select a trait to view all animals with the given trait</p>

<%!String driverName = "com.mysql.cj.jdbc.Driver";%>
<%!String DB_Location = "jdbc:mysql://127.0.0.1:3306/TermProject?";%>
<%!String DB_User = "root";%>
<%!String DB_Password = "admin";%>

<form action="ProcessAnimalTraitSearch">
	<%
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		// [ START: try/catch ]
		try {
			Class.forName(driverName);
			connect = DriverManager.getConnection(DB_Location, DB_User, DB_Password);
			
			String SQL_selectDistinctTraits = "SELECT DISTINCT trait FROM traits";
			
			preparedStatement = connect.prepareStatement(SQL_selectDistinctTraits);
			resultSet = preparedStatement.executeQuery();
	%>
	<p>Select Trait :
		<!-- Attribute "required" prevents a null-pointer exception -->
		<select name="traits" required="required">
			<%
				StringBuilder trait = new StringBuilder(" ");
				while (resultSet.next()) {
					String traitRaw = resultSet.getString("trait");
					char uppercase = Character.toUpperCase(traitRaw.charAt(0));
					trait = new StringBuilder(traitRaw);
					trait.setCharAt(0, uppercase);
					
					// Use StringBuilder.toString() for final output (See below)
			%>
			
			<option value="<%=trait.toString() %>"><%=trait %>
			</option>
			
			<%
				}
				// END while loop
			%>
		</select>
		<input type="submit" value="Search">
	</p>
	<%
		} catch (SQLException sqe) {
			out.println(sqe);
		} // END [ try/catch ]
	
	
	%>
</form>
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