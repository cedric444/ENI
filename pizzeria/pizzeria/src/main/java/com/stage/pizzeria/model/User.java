package com.stage.pizzeria.model;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Entity
public class User {

    @Column(name = "idUser")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    @Column(name = "nomUser")
    private String lastName;
    @Column(name = "prenomUser")
    private String firstName;
    @Column(name = "adresse")
    private String address;
    @Column(name = "codePostal")
    private String zipCode;
    @Column(name = "ville")
    private String city;
    @Column(name = "mailUser")
    private String email;
    @Column(name = "telUser")
    private String phoneNumber;
    @Column(name = "motDePasse")
    private String password;
    @Column(name = "nbPointidelite")
    private int fidelityPoints;
    @ManyToMany
    @JoinTable(
            name="usersRoles",
            joinColumns = @JoinColumn(
                    name="UserId"),
            inverseJoinColumns = @JoinColumn(
                    name="RoleId"))
    private Collection<Role> roles;
    @Timestamp
    private Date createdAt;
    @Timestamp
    private Date updatedAt;

    public User() {
    }

    public User(long id, String lastName, String firstName, String address, String zipCode, String city, String email, String phoneNumber, String password, int fidelityPoints, Date createdAt, Date updatedAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fidelityPoints = fidelityPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(long id, String lastName, String firstName, String address, String zipCode, String city, String email, String phoneNumber, String password, int fidelityPoints, Collection<Role> roles) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fidelityPoints = fidelityPoints;
        this.roles = roles;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id= id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFidelityPoints() {
        return fidelityPoints;
    }

    public void setFidelityPoints(int fidelityPoints) {
        this.fidelityPoints = fidelityPoints;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(lastName, user.lastName) && Objects.equals(firstName, user.firstName) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, firstName, email, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", address='" + address + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", fidelityPoints=" + fidelityPoints +
                ", roles=" + roles +
                '}';
    }
}
