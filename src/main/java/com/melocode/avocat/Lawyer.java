package com.melocode.avocat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;

public class Lawyer {
    private final int id;
    private final StringProperty name;
    private final StringProperty speciality;
    private final IntegerProperty experience;
    private final DoubleProperty rating;
    private final StringProperty address;

    public Lawyer(int id, String name, String speciality, int experience, double rating, String address) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.speciality = new SimpleStringProperty(speciality);
        this.experience = new SimpleIntegerProperty(experience);
        this.rating = new SimpleDoubleProperty(rating);
        this.address = new SimpleStringProperty(address);
    }
    
    // Constructeur simplifié pour GestionController
    public Lawyer(int id, String name, String speciality, String address) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.speciality = new SimpleStringProperty(speciality);
        this.experience = new SimpleIntegerProperty(0);
        this.rating = new SimpleDoubleProperty(0.0);
        this.address = new SimpleStringProperty(address);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getSpeciality() {
        return speciality.get();
    }

    public StringProperty specialityProperty() {
        return speciality;
    }

    public int getExperience() {
        return experience.get();
    }

    public IntegerProperty experienceProperty() {
        return experience;
    }

    public double getRating() {
        return rating.get();
    }

    public DoubleProperty ratingProperty() {
        return rating;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }
}
