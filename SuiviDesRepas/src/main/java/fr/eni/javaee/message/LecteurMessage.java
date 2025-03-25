package fr.eni.javaee.message;

import java.util.ResourceBundle;

public class LecteurMessage {

    private static ResourceBundle rb;

    static {
        try {
            rb = ResourceBundle.getBundle("message_erreur");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param code
     * @return
     */
    public static String getMessageErreur(int code) {
        String message = "";
        try {
            if(rb!=null) {
                message = rb.getString(String.valueOf(code));
            }else {
                message = "Problème lors de la lecture du fichier des messages d'erreur";
            }
        }catch(Exception e) {
            e.printStackTrace();
            message = "Une erreur inconnue est survenue";
        }
        return message;
    }

}

