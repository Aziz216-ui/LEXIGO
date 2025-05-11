package com.melocode.avocat;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import com.melocode.avocat.DatabaseConnection;
import com.melocode.avocat.Lawyer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationController {
    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;
    @FXML
    private Spinner<Integer> ageSpinner;

    @FXML
    private ComboBox<String> sexeComboBox;

    @FXML
    private DatePicker dateNaissancePicker;

    @FXML
    private DatePicker dateRdvPicker;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;



    @FXML
    private Label lawyerName;

    @FXML
    private Label statusLabel;

    private Lawyer selectedLawyer;

    public void setSelectedLawyer(Lawyer lawyer) {
        if (lawyer == null) {
            throw new IllegalArgumentException("Le lawyer ne peut pas Ãªtre null");
        }

        this.selectedLawyer = lawyer;
        if (lawyerName != null) {
            lawyerName.setText(lawyer.getName());
        } else {
            System.err.println("Le label lawyerName n'est pas initialisÃ©");
        }
    }

    @FXML
    private void initialize() {
        System.out.println("Initialisation du contrÃ´leur de rÃ©servation");

        // Initialiser le ComboBox de sexe
        sexeComboBox.getItems().addAll("Homme", "Femme");

        // Initialiser le Spinner d'Ã¢ge
        try {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 18);
            ageSpinner.setValueFactory(valueFactory);
            System.out.println("Spinner d'Ã¢ge initialisÃ©");
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation du Spinner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirm() {
        try {
            System.out.println("DÃ©but de la confirmation de rÃ©servation");
            System.out.println("Lawyer sÃ©lectionnÃ©: " + (selectedLawyer != null ? selectedLawyer.getName() : "null"));

            // VÃ©rifier si tous les champs sont remplis
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                    sexeComboBox.getValue() == null ||
                    dateNaissancePicker.getValue() == null || dateRdvPicker.getValue() == null ||
                    descriptionArea.getText().isEmpty()) {
                System.out.println("Champs manquants dÃ©tectÃ©s");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez remplir tous les champs obligatoires");
                alert.showAndWait();
                return;
            }

            // VÃ©rifier si l'Ã¢ge est valide
            int age = ageSpinner.getValueFactory().getValue();
            if (age <= 0 || age > 120) {
                System.out.println("Ã‚ge invalide: " + age);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("L'Ã¢ge doit Ãªtre entre 1 et 120 ans");
                alert.showAndWait();
                return;
            }

            // VÃ©rifier si les dates sont valides
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            LocalDate dateRdv = dateRdvPicker.getValue();
            if (dateNaissance.isAfter(dateRdv)) {
                System.out.println("Date de naissance aprÃ¨s date RDV");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("La date de naissance ne peut pas Ãªtre aprÃ¨s la date du rendez-vous");
                alert.showAndWait();
                return;
            }

            if (dateRdv.isBefore(LocalDate.now())) {
                System.out.println("Date RDV dans le passÃ©");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("La date du rendez-vous ne peut pas Ãªtre dans le passÃ©");
                alert.showAndWait();
                return;
            }

            // VÃ©rifier si la date de naissance est valide
            if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("La date de naissance ne peut pas Ãªtre dans le futur");
                alert.showAndWait();
                return;
            }

            // VÃ©rifier si la date du rendez-vous est valide
            if (dateRdvPicker.getValue().isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("La date du rendez-vous ne peut pas Ãªtre dans le passÃ©");
                alert.showAndWait();
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO reservations (lawyer_id, client_nom, client_prenom, client_age, client_sexe, client_date_naissance, date_rdv, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, selectedLawyer.getId());
                pstmt.setString(2, nomField.getText());
                pstmt.setString(3, prenomField.getText());
                pstmt.setInt(4, age);
                pstmt.setString(5, sexeComboBox.getValue());
                pstmt.setDate(6, java.sql.Date.valueOf(dateNaissancePicker.getValue()));
                pstmt.setDate(7, java.sql.Date.valueOf(dateRdvPicker.getValue()));
                pstmt.setString(8, descriptionArea.getText());
                pstmt.executeUpdate();

                // Envoyer l'email de confirmation
                String appointmentDate = dateRdvPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                EmailService.sendConfirmationEmail(selectedLawyer.getName(), appointmentDate);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("RÃ©servation confirmÃ©e avec succÃ¨s !");
                alert.showAndWait();

                // Retourner Ã  la page de recherche
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/search.fxml"));
                Parent root = loader.load();
                if (confirmButton.getScene() == null || confirmButton.getScene().getWindow() == null) {
                    Alert windowAlert = new Alert(Alert.AlertType.ERROR);
                    windowAlert.setTitle("Erreur");
                    windowAlert.setHeaderText(null);
                    windowAlert.setContentText("Erreur : la fenÃªtre n'est pas initialisÃ©e");
                    windowAlert.showAndWait();
                    return;
                }
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la rÃ©servation : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/search.fxml"));
            Parent root = loader.load();
            if (cancelButton.getScene() == null || cancelButton.getScene().getWindow() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur : la fenÃªtre n'est pas initialisÃ©e");
                alert.showAndWait();
                return;
            }
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du retour : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean validateFields() {
        if (dateRdvPicker.getValue() == null) {
            statusLabel.setText("Veuillez sÃ©lectionner une date");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        // Les validations pour email et tÃ©lÃ©phone ont Ã©tÃ© supprimÃ©es car ces champs ne sont plus utilisÃ©s

        if (nomField.getText().isEmpty()) {
            statusLabel.setText("Veuillez entrer un nom");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (prenomField.getText().isEmpty()) {
            statusLabel.setText("Veuillez entrer un prÃ©nom");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (ageSpinner.getValueFactory().getValue() <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un Ã¢ge");
            alert.showAndWait();
            return false;
        }

        if (sexeComboBox.getValue() == null) {
            statusLabel.setText("Veuillez sÃ©lectionner un sexe");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (dateNaissancePicker.getValue() == null) {
            statusLabel.setText("Veuillez sÃ©lectionner une date de naissance");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (descriptionArea.getText().isEmpty()) {
            statusLabel.setText("Veuillez entrer une description");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void returnToSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/avocat/search.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
