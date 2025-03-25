package fr.eni.javaee.servlet;

import fr.eni.javaee.exception.BusinessException;
import fr.eni.javaee.model.Repas;
import fr.eni.javaee.service.RepasService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/repas")
public class AfficherRepasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate dateFiltre = null;
        List<Integer> listeCodesErreur = new ArrayList<>();
        if(req.getParameter("dateFiltre")!=null && !req.getParameter("dateFiltre").isEmpty()) {
            try {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dateFiltre = LocalDate.parse(req.getParameter("dateFiltre"), dtf);
            }catch (DateTimeParseException e) {
                e.printStackTrace();
                listeCodesErreur.add(CodeExceptionServlet.FORMAT_DATE_ERREUR);
            }

            if(!listeCodesErreur.isEmpty()) {
                req.setAttribute("listeCodesErreur", listeCodesErreur);
            }else {
                try {
                    RepasService repasService = new RepasService();
                    List<Repas> listeRepas = null;
                    if(dateFiltre == null) {
                        listeRepas = repasService.getAllRepas();
                    }else {
                        listeRepas = repasService.getRepasByDate(dateFiltre);
                    }
                    req.setAttribute("listeRepas", listeRepas);
                }catch (BusinessException e) {
                    e.printStackTrace();
                    req.setAttribute("listeCodesErreur", e.getListeCodes());
                }
            }
            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/modules/historique.jsp");
            rd.forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
