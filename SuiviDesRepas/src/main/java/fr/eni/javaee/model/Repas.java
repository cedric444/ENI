package fr.eni.javaee.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Repas implements Serializable {

    private int id;
    private LocalDate jour;
    private LocalTime heure;
    private List<Aliment> listeAliments;

    public Repas(){
        this.listeAliments = new ArrayList<>();
    }

    public Repas(int id, LocalDate jour, LocalTime heure) {
        this.id = id;
        this.jour = jour;
        this.heure = heure;
    }

    public Repas(int id, LocalDate jour, LocalTime heure, List<Aliment> listeAliments){
        this.id = id;
        this.jour = jour;
        this.heure = heure;
        this.listeAliments = listeAliments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getJour() {
        return jour;
    }

    public void setJour(LocalDate jour) {
        this.jour = jour;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public List<Aliment> getListeAliments() {
        return listeAliments;
    }

    public void setListeAliments(List<Aliment> listeAliments) {
        this.listeAliments = listeAliments;
    }

    @Override
    public String toString() {
        return "Repas{" +
                "id=" + id +
                ", jour=" + jour +
                ", heure=" + heure +
                ", listeAliments=" + listeAliments +
                '}';
    }
}

