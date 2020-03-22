<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Application</title>
</head>
<body>

<div align="center">
	<!-- TODO: Edit page title according to User existence -->
	<h1>User Information Form</h1>
</div>

<!-- CHECK: (Aesthetics) Change to field sets? -->
<!-- [ FIELDSET/LEGEND REFERENCE: Web Dev p.416] -->

<div align="center">
    <c:if test="${user != null}">
	
	<!-- User DOES exist, UPDATE user -->
    <form action="update" method="post">
        </c:if>
		
		<!-- User does NOT exist, INSERT new user -->
        <c:if test="${user == null}">
        <form action="insert" method="post">
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
                    <input type="hidden" name="id" value="<c:out value='${user.username}' />"/>
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
                        <input type="text" name="email" size="45"
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
	
			<!-- User DOES exist -->
			<c:if test="${user != null}">
				<a href="index.jsp">Return to Home</a>
			</c:if>
			<!-- User does NOT exist -->
			<c:if test="${user == null}">
				<a href="Login.jsp">Return to Login</a>
			</c:if>
</div>
<div>

</div>
</body>
</html>