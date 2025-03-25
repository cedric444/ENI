package fr.eni.javaee.model;

public class Aliment {

    private int id;
    private String nom;
    private int idRepas;

    public Aliment(){}

    public Aliment(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Aliment(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdRepas() {
        return idRepas;
    }

    public void setIdRepas(int idRepas) {
        this.idRepas = idRepas;
    }

    @Override
    public String toString() {
        return "Aliment{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", idRepas=" + idRepas +
                '}';
    }
}

