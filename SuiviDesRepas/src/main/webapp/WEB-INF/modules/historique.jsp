<%@page import="fr.eni.javaee.model.Aliment"%>
<%@page import="fr.eni.javaee.model.Repas"%>
<%@page import="fr.eni.javaee.message.LecteurMessage"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<link href="<%=request.getContextPath() %>/css/style.css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Consultation</title>
</head>
<body>
    <h1>HISTORIQUE</h1>
    <div class="contenu">
        <%
            String dateFiltre="";
            if(request.getParameter("dateFiltre")!= null) {
                dateFiltre = request.getParameter("dateFiltre");
            }
        %>

        <form action="<%=request.getContextPath()%>/repas" method="post">
            <input type="date" name="dateFiltre" value="<%=dateFiltre%>"/>
            <input type="submit" value="Filtrer"/>
            <a href="<%=request.getContextPath()%>/repas"><input type="button" value="Réinitialiser"/></a>
        </form>

        <%
            List<Integer> listeCodesErreur = (List<Integer>)request.getAttribute("listeCodesErreur");
            if(listeCodesErreur != null) {
        %>
                <p classe="erreur">Erreur: </p>
        <%
                for(int code : listeCodesErreur) {
        %>
                    <p><%=LecteurMessage.getMessageErreur(code)%></p>
        <%
                }
            }
        %>

        <table>
            <thead>
                <tr>
                    <td>Date</td>
                    <td>Heure</td>
                    <td>Action</td>
                </tr>
            </thead>
                <%
                    List<Repas> listeRepas = (List<Repas>)request.getAttribute("listeRepas");
                    if(listeRepas != null && listeRepas.size() > 0) {
                %>
                        <tbody>
                            <%
                                for(Repas repas : listeRepas) {
                            %>
                                    <tr>
                                        <td><%=repas.getJour()%></td>
                                        <td><%=repas.getHeure()%></td>
                                        <td><a href="<%=request.getContextPath()%>/repas?detail=<%=repas.getId()%>&<%=dateFiltre%>">Détails</a></td>
                                    </tr>
                                <%
                                    if(String.valueOf(repas.getId()).equals(request.getParameter("detail"))) {
                                %>
                                        <tr>
                                            <td colspan="3">
                                                <ul>
                                                    <%
                                                        for(Aliment aliment : repas.getListeAliments()) {
                                                    %>
                                                            <li><%=aliment.getNom()%></li>
                                                    <%
                                                        }
                                                    %>
                                                </ul>
                                            </td>
                                        </tr>
                                <%
                                    }
                                }
                                %>
                            </tbody>
                <%
                    }else {
                %>
                        <p>Il n'y a aucun repas à afficher</p>
                <%
                    }
                %>
        </table>

        <a href="<%=request.getContextPath()%>/ajoutRepas"><input type="button" value="Ajouter un nouveau repas"/></a>
        <a href="<%=request.getContextPath()%>"><input type="button" value="Retour à l'accueil"/></a>
    </div>
</body>
</html>