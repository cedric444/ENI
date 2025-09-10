package com.stage.pizzeria.model;

import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class TypeProduit {

    private int id;
    private String name;

    public TypeProduit() {
    }

    public TypeProduit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TypeProduit that = (TypeProduit) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "TypeProduit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
