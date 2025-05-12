package com.melocode.avocat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AvocatLoginController {
    @FXML
    private TextField nomAvocatField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String nomAvocat = nomAvocatField.getText().trim();

        if (nomAvocat.isEmpty()) {
            errorLabel.setText("Veuillez entrer un nom");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si l'avocat existe dans la base de données
            String query = "SELECT id FROM lawyers WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomAvocat);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Avocat trouvé, charger l'interface des rendez-vous
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/rendez_vous_avocat.fxml"));
                Parent root = loader.load();

                // Passer le nom de l'avocat au contrôleur suivant
                RendezVousAvocatController controller = loader.getController();
                controller.setNomAvocat(nomAvocat);

                Stage stage = (Stage) nomAvocatField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Rendez-vous de " + nomAvocat);
            } else {
                errorLabel.setText("Aucun avocat trouvé avec ce nom");
            }
        } catch (Exception e) {
            errorLabel.setText("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/action.fxml"));
            Parent actionView = loader.load();

            Stage stage = (Stage) nomAvocatField.getScene().getWindow();
            stage.setScene(new Scene(actionView));
            stage.setTitle("Système de Gestion d'Avocats");
        } catch (Exception e) {
            errorLabel.setText("Erreur lors du retour : " + e.getMessage());
            e.printStackTrace();
        }
    }
}