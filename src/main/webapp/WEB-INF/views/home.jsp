<%@ page language="java" contentType="text/html;charset=Windows-31J"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page import="java.util.*" %>
<%@ page import="jp.co.test.UserModel" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!
</h1>

<P>  The time on the server is ${serverTime}. </P>

<ul>
	<c:forEach var="user" items="${userList}">
		<li>${user.getName()}</li>
	</c:forEach>
</ul>

</body>
</html>
