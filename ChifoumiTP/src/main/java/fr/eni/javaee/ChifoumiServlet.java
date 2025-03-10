package fr.eni.javaee;

import java.io.IOException;
import java.util.Random;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ChifoumiServlet
 */
@WebServlet("/ChifoumiServlet")
public class ChifoumiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ChifoumiServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/modules/jeu.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String player = request.getParameter("value");
		String[] values = {"pierre", "feuille", "ciseaux"};
		String computer = "";
		int range = values.length-0;
		for(int i=0; i<range; i++) {
			int random = (int) (Math.random()*range) + 0;
			computer = values[random];
		}
		String result = "";
		System.out.println(player);
		System.out.println(computer);
		if( player.equals(computer)) {
			result ="Egalité!!!";
		}
		switch(player) {
		case "pierre" :
			if(computer.equals("ciseaux")) {
				result = "Vous avez gagné!!!";
			}else if(computer.equals("feuille")) {
				result = "Vous avez perdu...";
			}
			break;
		case "feuille" :
			if(computer.equals("pierre")) {
				result = "Vous avez gagné!!!";
			}else if(computer.equals("ciseaux")) {
				result = "vous avez perdu..."; 
			}
			break;
		case "ciseaux" :
			if(computer.equals("feuille")) {
				result = "Vous avez gagné!!!";
			}else if(computer.equals("pierre")) {
				result = "vous avez perdu...";
			}
		}
			
		request.setAttribute("result", result);
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/modules/resultat.jsp");
		rd.forward(request, response);
	}

}
