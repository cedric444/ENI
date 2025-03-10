<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%!
	private String value;
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
%>
<html>
<head>
<meta charset="UTF-8">
<title>Jeu du Chifoumi</title>
</head>
<body>
	<form method="post" action="ChifoumiServlet">
		<%
			this.setValue(this.getValue()); 
		%>
		<input type="text" placeholder="entrez pierre, feuille ou ciseaux" value="<%=this.getValue()==null? "" : this.getValue()%>" name="value"/>
		<button type="submit">Valider</button>
	</form>
</body>
</html>