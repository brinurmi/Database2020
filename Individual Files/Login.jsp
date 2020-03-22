<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <!-- TODO: Style the forms (Web Dev p. 419) -->
    <title>User Login :: Adoption</title>
    <style>
		#darktheme {
			background-color: #5c5c5c;
			color: #FFFFFF;
		}
		
		#loginform {
			float: left;
            font-family: Arial, sans-serif;
            padding: 10px;
        }

        label {
            float: left;
            width: 150px;
            clear: left;
            color: #942c27;
            text-align: right;
            padding-right: 10px;
            margin-top: 10px;
        }

        input {
            margin-top: 10px;
            display: block;
        }
		
		a {
			color: #499252;
			text-decoration: none;
		}
				
		a:hover {
			color: #ccff99;
		}
		
    </style>
</head>
<body id="darktheme">

<header align="center">
    <h1>User Login</h1>
</header>
<div>
    <h4>Enter your Username and Password</h4>
	<h5 style="color: #942c27">*Required</h5>
	
    <div>
    <fieldset>
    <legend>User Login</legend>
    	<!-- If-time: Make only asterisks red -->
        <!-- Method set to POST: Login info hidden during transmission to -->
        <form action="LoginAttempt" method="post" id="loginform">
            <label for="username" >* Username:</label>
            <input id="username" name="username" type="text">

            <label for="password">* Password</label>
            <input id="password" name="password" type="password">
            
            <input type="submit" value="Login" align="middle">
            
			<br>
            <a href="UsersForm.jsp">Don't have an Account? Click Here to Register.</a>
        </form>
        </fieldset>
    </div>
</div>
</body>
</html>