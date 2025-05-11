package com.melocode.avocat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GestionController implements Initializable {

    @FXML private ComboBox<Lawyer> avocatComboBox;
    @FXML private TextField nomClientField;
    @FXML private TextField prenomClientField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private Spinner<Integer> ageSpinner;
    @FXML private DatePicker dateRdvPicker;
    @FXML private TextArea descriptionArea;
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private Button afficherBtn;
    @FXML private Button retourBtn;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> avocatColumn;
    @FXML private TableColumn<Reservation, String> nomClientColumn;
    @FXML private TableColumn<Reservation, String> prenomClientColumn;
    @FXML private TableColumn<Reservation, String> sexeColumn;
    @FXML private TableColumn<Reservation, LocalDate> dateNaissanceColumn;
    @FXML private TableColumn<Reservation, Integer> ageColumn;
    @FXML private TableColumn<Reservation, LocalDate> dateRdvColumn;
    @FXML private TableColumn<Reservation, String> descriptionColumn;
    @FXML private Label messageLabel;

    private String adminUsername;
    private ObservableList<Reservation> reservationsList = FXCollections.observableArrayList();
    private Map<Integer, Lawyer> lawyersMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuration du ComboBox avocat pour afficher le nom
        avocatComboBox.setConverter(new StringConverter<Lawyer>() {
            @Override
            public String toString(Lawyer lawyer) {
                return lawyer != null ? lawyer.getName() : "";
            }

            @Override
            public Lawyer fromString(String string) {
                return null;
            }
        });

        // Configuration du Spinner pour l'âge
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 30);
        ageSpinner.setValueFactory(valueFactory);
        ageSpinner.setEditable(true);

        // Initialisation des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        avocatColumn.setCellValueFactory(new PropertyValueFactory<>("avocatNom"));
        nomClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        prenomClientColumn.setCellValueFactory(new PropertyValueFactory<>("prenomClient"));
        sexeColumn.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        dateRdvColumn.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Initialisation du ComboBox sexe
        sexeComboBox.setItems(FXCollections.observableArrayList("Homme", "Femme"));

        // Chargement des avocats
        chargerAvocats();

        // Configuration de la sélection dans le tableau
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherDetailsReservation(newSelection);
            }
        });

        // Chargement des réservations existantes
        chargerReservations();
    }

    public void initData(String adminUsername) {
        this.adminUsername = adminUsername;
        messageLabel.setText("Bienvenue, " + adminUsername);
        messageLabel.setTextFill(Color.GREEN);
    }

    @FXML
    private void handleAjouter() {
        if (!validerChamps()) {
            return;
        }

        try {
            Lawyer avocat = avocatComboBox.getValue();
            if (avocat == null) {
                messageLabel.setText("Veuillez sélectionner un avocat");
                messageLabel.setTextFill(Color.RED);
                return;
            }

            // Correction: Utiliser les noms de colonnes corrects de la base de données
            String query = "INSERT INTO reservations (lawyer_id, client_nom, client_prenom, client_sexe, client_date_naissance, client_age, date_rdv, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, avocat.getId());
                pstmt.setString(2, nomClientField.getText().trim());
                pstmt.setString(3, prenomClientField.getText().trim());
                pstmt.setString(4, sexeComboBox.getValue());
                pstmt.setDate(5, java.sql.Date.valueOf(dateNaissancePicker.getValue()));
                pstmt.setInt(6, ageSpinner.getValue());
                pstmt.setDate(7, java.sql.Date.valueOf(dateRdvPicker.getValue()));
                pstmt.setString(8, descriptionArea.getText().trim());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    messageLabel.setText("Réservation ajoutée avec succès");
                    messageLabel.setTextFill(Color.GREEN);
                    viderChamps();
                    chargerReservations();
                } else {
                    messageLabel.setText("Échec de l'ajout de la réservation");
                    messageLabel.setTextFill(Color.RED);
                }
            }

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de l'ajout: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        } catch (Exception e) {
            messageLabel.setText("Erreur inattendue: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            messageLabel.setText("Veuillez sélectionner une réservation à modifier");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (!validerChamps()) {
            return;
        }

        // Correction: Utiliser les noms de colonnes corrects de la base de données
        String query = "UPDATE reservations SET lawyer_id=?, client_nom=?, client_prenom=?, client_sexe=?, client_date_naissance=?, client_age=?, date_rdv=?, description=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            Lawyer avocat = avocatComboBox.getValue();
            pstmt.setInt(1, avocat.getId());
            pstmt.setString(2, nomClientField.getText().trim());
            pstmt.setString(3, prenomClientField.getText().trim());
            pstmt.setString(4, sexeComboBox.getValue());
            pstmt.setDate(5, java.sql.Date.valueOf(dateNaissancePicker.getValue()));
            pstmt.setInt(6, ageSpinner.getValue());
            pstmt.setDate(7, java.sql.Date.valueOf(dateRdvPicker.getValue()));
            pstmt.setString(8, descriptionArea.getText().trim());
            pstmt.setInt(9, selectedReservation.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                messageLabel.setText("Réservation modifiée avec succès");
                messageLabel.setTextFill(Color.GREEN);
                chargerReservations();
            } else {
                messageLabel.setText("Aucune modification effectuée");
                messageLabel.setTextFill(Color.ORANGE);
            }

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la modification: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        Reservation selectedReservation = reservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            messageLabel.setText("Veuillez sélectionner une réservation à supprimer");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String query = "DELETE FROM reservations WHERE id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, selectedReservation.getId());
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    messageLabel.setText("Réservation supprimée avec succès");
                    messageLabel.setTextFill(Color.GREEN);
                    viderChamps();
                    chargerReservations();
                } else {
                    messageLabel.setText("Échec de la suppression");
                    messageLabel.setTextFill(Color.RED);
                }

            } catch (SQLException e) {
                messageLabel.setText("Erreur lors de la suppression: " + e.getMessage());
                messageLabel.setTextFill(Color.RED);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAfficher() {
        try {
            // Correction: Utiliser les noms de colonnes corrects de la base de données
            String query = "SELECT r.*, l.name as avocat_nom, l.id as lawyer_id FROM reservations r " +
                    "JOIN lawyers l ON r.lawyer_id = l.id ORDER BY r.date_rdv DESC";

            reservationsList.clear();

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getInt("id"),
                            rs.getInt("lawyer_id"),
                            rs.getString("avocat_nom"),
                            rs.getString("client_nom"),
                            rs.getString("client_prenom"),
                            rs.getString("client_sexe"),
                            rs.getDate("client_date_naissance").toLocalDate(),
                            rs.getInt("client_age"),
                            rs.getDate("date_rdv").toLocalDate(),
                            rs.getString("description")
                    );
                    reservationsList.add(reservation);
                }

                reservationsTable.setItems(reservationsList);
                messageLabel.setText("Affichage de toutes les réservations (" + reservationsList.size() + " réservations)");
                messageLabel.setTextFill(Color.GREEN);
            }
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des réservations: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/action.fxml"));
            Parent actionView = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(actionView);
            stage.setScene(scene);
            stage.setTitle("Système de Gestion d'Avocats");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour à l'écran principal: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void chargerAvocats() {
        String query = "SELECT id, name, speciality FROM lawyers";
        ObservableList<Lawyer> avocats = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Lawyer avocat = new Lawyer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("speciality"),
                        "" // L'adresse n'est pas nécessaire pour l'affichage
                );
                avocats.add(avocat);
                lawyersMap.put(avocat.getId(), avocat);
            }

            avocatComboBox.setItems(avocats);

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des avocats: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void chargerReservations() {
        // Correction: Utiliser les noms de colonnes corrects de la base de données
        String query = "SELECT r.*, l.name as avocat_nom, l.id as lawyer_id FROM reservations r " +
                "JOIN lawyers l ON r.lawyer_id = l.id ORDER BY r.date_rdv DESC LIMIT 20";
        reservationsList.clear();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("lawyer_id"),
                        rs.getString("avocat_nom"),
                        rs.getString("client_nom"),
                        rs.getString("client_prenom"),
                        rs.getString("client_sexe"),
                        rs.getDate("client_date_naissance").toLocalDate(),
                        rs.getInt("client_age"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("description")
                );
                reservationsList.add(reservation);
            }

            reservationsTable.setItems(reservationsList);

        } catch (SQLException e) {
            messageLabel.setText("Erreur lors du chargement des réservations: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    private void afficherDetailsReservation(Reservation reservation) {
        for (Lawyer avocat : avocatComboBox.getItems()) {
            if (avocat.getId() == reservation.getLawyerId()) {
                avocatComboBox.setValue(avocat);
                break;
            }
        }

        nomClientField.setText(reservation.getNomClient());
        prenomClientField.setText(reservation.getPrenomClient());
        sexeComboBox.setValue(reservation.getSexe());
        dateNaissancePicker.setValue(reservation.getDateNaissance());
        ageSpinner.getValueFactory().setValue(reservation.getAge());
        dateRdvPicker.setValue(reservation.getDateRdv());
        descriptionArea.setText(reservation.getDescription());
    }

    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();

        if (avocatComboBox.getValue() == null) {
            erreurs.append("Veuillez sélectionner un avocat.\n");
        }
        if (nomClientField.getText().trim().isEmpty()) {
            erreurs.append("Le nom du client est requis.\n");
        }
        if (prenomClientField.getText().trim().isEmpty()) {
            erreurs.append("Le prénom du client est requis.\n");
        }
        if (sexeComboBox.getValue() == null) {
            erreurs.append("Veuillez sélectionner le sexe du client.\n");
        }
        if (dateNaissancePicker.getValue() == null) {
            erreurs.append("La date de naissance est requise.\n");
        }
        if (ageSpinner.getValue() == null || ageSpinner.getValue() < 1) {
            erreurs.append("L'âge du client est requis et doit être valide.\n");
        }
        if (dateRdvPicker.getValue() == null) {
            erreurs.append("La date du rendez-vous est requise.\n");
        }

        if (erreurs.length() > 0) {
            messageLabel.setText(erreurs.toString());
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }

    private void viderChamps() {
        avocatComboBox.setValue(null);
        nomClientField.clear();
        prenomClientField.clear();
        sexeComboBox.setValue(null);
        dateNaissancePicker.setValue(null);
        ageSpinner.getValueFactory().setValue(30); // Valeur par défaut
        dateRdvPicker.setValue(null);
        descriptionArea.clear();
    }
}