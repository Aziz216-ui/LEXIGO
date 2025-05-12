package com.melocode.avocat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class RendezVousAvocatController {
    @FXML
    private Label titreLabel;

    @FXML
    private TableView<Reservation> rendezVousTableView;

    @FXML
    private TableColumn<Reservation, Integer> idColumn;

    @FXML
    private TableColumn<Reservation, String> nomClientColumn;

    @FXML
    private TableColumn<Reservation, String> prenomClientColumn;

    @FXML
    private TableColumn<Reservation, Integer> ageColumn;

    @FXML
    private TableColumn<Reservation, String> sexeColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> dateNaissanceColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> dateRdvColumn;

    @FXML
    private TableColumn<Reservation, String> descriptionColumn;

    private String nomAvocat;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        prenomClientColumn.setCellValueFactory(new PropertyValueFactory<>("prenomClient"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        sexeColumn.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        dateNaissanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        dateRdvColumn.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    public void setNomAvocat(String nomAvocat) {
        this.nomAvocat = nomAvocat;
        titreLabel.setText("Rendez-vous de " + nomAvocat);
        chargerRendezVous();
    }

    private void chargerRendezVous() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Requête pour récupérer les rendez-vous de l'avocat
            String query = "SELECT r.* FROM reservations r " +
                    "JOIN lawyers l ON r.lawyer_id = l.id " +
                    "WHERE l.name = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomAvocat);

            ResultSet rs = pstmt.executeQuery();

            ObservableList<Reservation> rendezVousList = FXCollections.observableArrayList();

            while (rs.next()) {
                Reservation rdv = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("lawyer_id"),
                        nomAvocat,
                        rs.getString("client_nom"),
                        rs.getString("client_prenom"),
                        rs.getString("client_sexe"),
                        rs.getDate("client_date_naissance").toLocalDate(),
                        rs.getInt("client_age"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("description")
                );
                rendezVousList.add(rdv);
            }

            rendezVousTableView.setItems(rendezVousList);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des rendez-vous : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/avocat_login.fxml"));
            Parent actionView = loader.load();

            Stage stage = (Stage) rendezVousTableView.getScene().getWindow();
            stage.setScene(new Scene(actionView));
            stage.setTitle("Connexion Avocat");
        } catch (Exception e) {
            System.err.println("Erreur lors du retour : " + e.getMessage());
            e.printStackTrace();
        }
    }
}