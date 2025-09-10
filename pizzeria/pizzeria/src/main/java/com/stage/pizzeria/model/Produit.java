package com.stage.pizzeria.model;

import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class Produit {

    private Long id;
    private String name;
    private float price;
    private int quantity;
    private TypeProduit typeProduit;

    public Produit() {
    }

    public Produit(Long id, String name, float price, int quantity, TypeProduit typeProduit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.typeProduit = typeProduit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public TypeProduit getTypeProduit() {
        return typeProduit;
    }

    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Produit produit = (Produit) o;
        return Objects.equals(id, produit.id) && Objects.equals(name, produit.name) && Objects.equals(typeProduit, produit.typeProduit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, typeProduit);
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", typeProduit=" + typeProduit +
                '}';
    }
}
