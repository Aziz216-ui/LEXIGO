package com.melocode.avocat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdentificationController {

    @FXML
    private TextField adminUsernameField;

    @FXML
    private Button validerBtn;

    @FXML
    private Button retourBtn;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleValidation(ActionEvent event) {
        String adminId = adminUsernameField.getText().trim();

        // Validation du champ vide
        if (adminId.isEmpty()) {
            errorLabel.setText("Veuillez entrer un identifiant administrateur.");
            errorLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Vérifier si l'identifiant "243JMT505" est saisi
            if (adminId.equals("243JMT505") || verifyAdmin(adminId)) {
                loadGestionInterface(event, adminId);
            } else {
                errorLabel.setText("Identifiant administrateur introuvable.");
                errorLabel.setTextFill(Color.RED);
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur de connexion à la base de données: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    private boolean verifyAdmin(String adminId) throws SQLException {
        String query = "SELECT * FROM admins WHERE identifiant = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, adminId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retourne true si un résultat est trouvé
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la vérification de l'administrateur: " + e.getMessage());
            throw e;
        }
    }

    private void loadGestionInterface(ActionEvent event, String adminId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion.fxml"));
            Parent root = loader.load();

            GestionController controller = loader.getController();
            controller.initData(adminId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Réservations");

            // Configuration de la fenêtre
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);

            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Erreur de chargement de l'interface: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/action.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil");
            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Erreur lors du retour à l'accueil: " + e.getMessage());
            errorLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }
}