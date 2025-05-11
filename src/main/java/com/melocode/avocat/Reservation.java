package com.melocode.avocat;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int lawyerId;
    private String avocatNom;
    private String nomClient;
    private String prenomClient;
    private String sexe;
    private LocalDate dateNaissance;
    private int age;
    private LocalDate dateRdv;
    private String description;

    public Reservation(int id, int lawyerId, String avocatNom, String nomClient, String prenomClient,
                       String sexe, LocalDate dateNaissance, int age, LocalDate dateRdv, String description) {
        this.id = id;
        this.lawyerId = lawyerId;
        this.avocatNom = avocatNom;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.age = age;
        this.dateRdv = dateRdv;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getLawyerId() {
        return lawyerId;
    }

    public String getAvocatNom() {
        return avocatNom;
    }

    public String getNomClient() {
        return nomClient;
    }

    public String getPrenomClient() {
        return prenomClient;
    }

    public String getSexe() {
        return sexe;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public int getAge() {
        return age;
    }

    public LocalDate getDateRdv() {
        return dateRdv;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setLawyerId(int lawyerId) {
        this.lawyerId = lawyerId;
    }

    public void setAvocatNom(String avocatNom) {
        this.avocatNom = avocatNom;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDateRdv(LocalDate dateRdv) {
        this.dateRdv = dateRdv;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}