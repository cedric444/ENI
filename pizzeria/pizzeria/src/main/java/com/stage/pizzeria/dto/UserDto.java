package com.stage.pizzeria.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Date;


public class UserDto {

    long id;
    @NotBlank(message="Le nom est obligatoire")
    @Pattern(regexp= "([A-Za-z]+[-' ]?){3,50}", message = "Le nom doit comporter au moins 3 lettres")
    String lastName;
    @NotBlank(message="Le prénom est obligatoire")
    @Pattern(regexp= "([A-Za-z]+-?){3,50}", message = "Le prénom doit comporter au moins 3 lettres")
    String firstName;
    @NotBlank(message="L'adresse est obligatoire")
    String address;
    @NotBlank(message="Le code postal est obligatoire")
    @Pattern(regexp="[0-9]{5}")
    String zipCode;
    @NotBlank(message="La ville est oligatoire")
    String city;
    @NotBlank(message="L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email;
    @NotBlank(message="Le numéro de téléphone est obligatoire")
    @Pattern(regexp="^[0]{1}[1-9]{1}[0-9]{8}")
    String phoneNumber;
    @NotBlank(message="Le mot de passe est obligatoire")
    @Size(min = 8)
    String password;
    int fidelityPoints=0;
    Date createdAt;
    Date updatedAt;

    public UserDto() {
    }

    public UserDto(long id, String lastName, String firstName, String address, String zipCode, String city, String email, String phoneNumber, String password, int fidelityPoints, Date createdAt, Date updatedAt) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
