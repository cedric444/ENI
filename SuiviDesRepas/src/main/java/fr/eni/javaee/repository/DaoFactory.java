package fr.eni.javaee.repository;

public abstract class DaoFactory {

    public static RepasDao getRepasDao() {
        return new RepasDaoImpl();
    }
}
