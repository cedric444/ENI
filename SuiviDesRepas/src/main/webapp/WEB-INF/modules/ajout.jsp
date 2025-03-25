<%@page import="fr.eni.javaee.message.LecteurMessage"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ajout</title>
</head>
<body>
    <h1>AJOUT DUN REPAS</h1>
    <div class="contenu">
        <%
            List<Integer> listeCodesErreur = (List<Integer>)request.getAttribute("listeCodesErreur");
            if(listeCodesErreur != null) {
        %>
                <p class="erreur">"Erreur, le repas n'a pas pu être ajouté:"</p>
        <%
                for(int code : listeCodesErreur) {
        %>
                    <p><%=LecteurMessage.getMessageErreur(code)%><p>
        <%
                }
            }
        %>
        <form action="<%=request.getContextPath()%>/ajoutRepas" method="post">
            <div class="saisie">
                <label for="jour">date: </label>
                <input type="date" name="jour" id="jour" value="<%=request.getParameter("jour")%>"/>
            </div>
                <label for="heure">Heure: </label>
                <input type="time" name="heure" id="heure" value="<%=request.getParameter("heure")%>"/>
            <div class="saisie">
            </div>
            <div class="saisie">
                <label for="repas">Repas: </label>
                <input type="textarea" name="repas" id="repas" value="<%=request.getParameter("repas")!=null?request.getParameter("repas"):""%>"></textarea>
            </div>
            <div>
                <input type="submit" value="Valider">
                <a href="<%=request.getContextPath()%>/"><input type="button" value="Annuler"></a>
            </div>
        </form>
    </div>
</body>
</html>