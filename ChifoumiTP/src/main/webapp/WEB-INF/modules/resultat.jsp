<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Jeu du Chifoumi</title>
</head>
<body>
	<% 
		String result = (String) request.getAttribute("result");	
	%>
	<%=result%>
	<a href="<%=request.getContextPath()%>/ChifoumiServlet"s>Rejouer</a>
</body>
</html>