package com.melocode.avocat;

import com.melocode.avocat.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * FXML Controller class
 *
 * @author melocode
 */

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Tentative de connexion avec l'email : " + email);

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            if (validateLogin(email, password)) {
                System.out.println("Connexion réussie pour l'utilisateur : " + email);
                try {
                    // Stocker l'email de l'utilisateur dans la session
                    UserSession.getInstance().setUserEmail(email);
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/search.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    if (emailField.getScene() == null || emailField.getScene().getWindow() == null) {
                        errorLabel.setText("Erreur : la fenêtre n'est pas initialisée");
                        return;
                    }
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    stage.setScene(scene);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'interface : " + e.getMessage());
                    errorLabel.setText("Erreur lors du chargement de l'interface");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Échec de la connexion pour l'utilisateur : " + email);
                errorLabel.setText("Email ou mot de passe incorrect");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du login : " + e.getMessage());
            errorLabel.setText("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // Charger l'interface action.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/action.fxml"));
            Parent actionView = loader.load();
            
            // Obtenir la scène actuelle et changer son contenu
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(actionView);
            stage.setScene(scene);
            stage.setTitle("Système de Gestion d'Avocats");
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du retour à l'écran principal : " + e.getMessage());
            errorLabel.setText("Erreur lors du retour à l'écran principal");
            e.printStackTrace();
        }
    }

    private boolean validateLogin(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connexion à la base de données établie pour la validation");
            
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            System.out.println("Exécution de la requête : " + query);
            System.out.println("Paramètres : email=" + email + ", password=" + password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userName = rs.getString("name");
                System.out.println("Utilisateur trouvé : " + userName);
                UserSession.getInstance().setUserName(userName);
                return true;
            }
            System.out.println("Aucun utilisateur trouvé avec ces identifiants");
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du login : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la validation du login", e);
        }
    }
}
