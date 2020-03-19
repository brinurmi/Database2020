<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search by Trait</title>
</head>
<body>
    <h1>Search For Adoptable Animals</h1>
    <p>Please select a trait to view all animals with the given trait</p>

<%! String driverName = "com.mysql.cj.jdbc.Driver";%>
<%!String url = "jdbc:mysql://127.0.0.1:3306/TermProject?";%>
<%!String user = "root";%>
<%!String psw = "admin";%>
<form action="DynamicDropResult.jsp">
<%
Connection con = null;
PreparedStatement ps = null;
try
{
	Class.forName(driverName);
	con = DriverManager.getConnection(url,user,psw);
	String sql = "SELECT * FROM animals";
	ps = con.prepareStatement(sql);
	ResultSet rs = ps.executeQuery();	
	
	
		%>
		<p>Select Trait : 
		<select name = "traits">
		<%
		while(rs.next())
		{
			String traits = rs.getString("traits");			
		%>
		<option value="<%=traits %>"><%=traits %></option>
		<%
		}
		%>
		</select>
		<input type="submit" value="Enter">
		</p>
		<%
		
	
}
catch(SQLException sqe)
{	
	out.println(sqe);
}
%>


</form>
</body>
</html>
