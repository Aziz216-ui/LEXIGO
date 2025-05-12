package com.melocode.avocat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class ActionController {

    @FXML
    private Button reservationBtn;

    @FXML
    private Button administrationBtn;

    /**
     * Handles the Reservation button click.
     * Redirects to the login screen.
     */
    @FXML
    private void handleReservation(ActionEvent event) {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginView = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.setTitle("Connexion Utilisateur");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface de login : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Administration button click.
     * Redirects to the admin identification screen.
     */
    @FXML
    private void handleAdministration(ActionEvent event) {
        try {
            // Load the identification FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/identification.fxml"));
            Parent identificationView = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene
            Scene scene = new Scene(identificationView);
            stage.setScene(scene);
            stage.setTitle("Identification Administrateur");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface d'identification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Lawyer Appointments button click.
     * Redirects to the lawyer login screen.
     */
    @FXML
    private void handleRendezVousAvocat(ActionEvent event) {
        try {
            // Load the lawyer appointments login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/avocat_login.fxml"));
            Parent loginView = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.setTitle("Connexion Avocat");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface de connexion avocat : " + e.getMessage());
            e.printStackTrace();
        }
    }
}