package fr.eni.javaee.servlet;

import fr.eni.javaee.exception.BusinessException;
import fr.eni.javaee.service.RepasService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/ajoutRepas")
public class AjoutRepasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/modules/ajout.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Lecture des paramètres
        LocalDate jour = null;
        LocalTime heure = null;
        String repas = "";
        List<Integer> listeCodesErreur = new ArrayList<>();

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            jour= LocalDate.parse(req.getParameter("jour"), dtf);
        }catch (DateTimeParseException e) {
            e.printStackTrace();
            listeCodesErreur.add(CodeExceptionServlet.FORMAT_DATE_ERREUR);
        }

        try {
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("HH:mm");
            heure = LocalTime.parse(req.getParameter("heure"), dtf1);
        }catch (DateTimeParseException e) {
            e.printStackTrace();
            listeCodesErreur.add(CodeExceptionServlet.FORMAT_HEURE_ERREUR);
        }

        repas = req.getParameter("repas");
        if(repas == null || repas.trim().isEmpty()) {
            listeCodesErreur.add(CodeExceptionServlet.FORMAT_ALIMENT_ERREUR);
        }

        //Traitement
        if(!listeCodesErreur.isEmpty()) {
            //On renvoie les codes d'erreur
            req.setAttribute("listeCodesErreur", listeCodesErreur);
            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/modules/ajout.jsp");
            rd.forward(req, resp);
        }else {
            //On ajoute le repas
            RepasService repasService = new RepasService();
            try {
                System.out.println(jour);
                repasService.addRepas(jour, heure, Arrays.asList(repas.split(",")));
                RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/modules/historique.jsp");
                rd.forward(req,resp);
            }catch(BusinessException e) {
                e.printStackTrace();
                req.setAttribute("listeCodesErreur", e.getListeCodes());
                RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/modules/ajout.jsp");
                rd.forward(req, resp);
            }
        }

    }
}
