package com.melocode.avocat;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.melocode.avocat.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.melocode.avocat.Lawyer;
import java.util.ArrayList;
import java.util.List;

public class SearchController {
    @FXML
    private ComboBox<String> specialityComboBox;

    @FXML
    private Spinner<Integer> experienceSpinner;

    @FXML
    private TableView<Lawyer> lawyersTable;

    @FXML
    private TableColumn<Lawyer, String> nameColumn;

    @FXML
    private TableColumn<Lawyer, String> specialityColumn;

    @FXML
    private TableColumn<Lawyer, Integer> experienceColumn;

    @FXML
    private TableColumn<Lawyer, Double> ratingColumn;

    @FXML
    private Button reservationButton;

    @FXML
    public void initialize() {
        // Initialiser les spécialités
        ObservableList<String> specialities = FXCollections.observableArrayList(
                "Droit civil", "Droit pénal", "Droit commercial", "Droit du travail",
                "Droit immobilier", "Droit de la famille", "Droit des affaires"
        );
        specialityComboBox.setItems(specialities);

        // Configurer les colonnes de la table
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        specialityColumn.setCellValueFactory(cellData -> cellData.getValue().specialityProperty());
        experienceColumn.setCellValueFactory(cellData -> cellData.getValue().experienceProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());

        // Désactiver le bouton Réserver par défaut
        reservationButton.setDisable(true);

        // Ajouter un écouteur pour activer/désactiver le bouton Réserver
        lawyersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            reservationButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleSearch() {
        String speciality = specialityComboBox.getValue();
        int minExperience = experienceSpinner.getValue();

        List<Lawyer> lawyers = searchLawyers(speciality, minExperience);
        lawyersTable.setItems(FXCollections.observableArrayList(lawyers));
    }

    @FXML
    private void handleReservation() {
        System.out.println("Début de handleReservation");
        
        Lawyer selectedLawyer = lawyersTable.getSelectionModel().getSelectedItem();
        if (selectedLawyer == null) {
            System.out.println("Aucun avocat sélectionné");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez d'abord sélectionner un avocat dans la liste.");
            alert.showAndWait();
            return;
        }
        System.out.println("Avocat sélectionné: " + selectedLawyer.getName());

        try {
            System.out.println("Chargement du FXML de réservation");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation.fxml"));
            Parent root = loader.load();
            System.out.println("FXML chargé avec succès");

            ReservationController controller = loader.getController();
            if (controller == null) {
                System.out.println("Contrôleur null");
                throw new Exception("Impossible de charger le contrôleur de réservation");
            }
            System.out.println("Contrôleur chargé");
            
            controller.setSelectedLawyer(selectedLawyer);
            
            Scene scene = new Scene(root);
            if (lawyersTable.getScene() == null || lawyersTable.getScene().getWindow() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur : la fenêtre n'est pas initialisée");
                alert.showAndWait();
                return;
            }
            Stage stage = (Stage) lawyersTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Réservation avec " + selectedLawyer.getName());
            System.out.println("Navigation vers la page de réservation réussie");
            
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private List<Lawyer> searchLawyers(String speciality, int minExperience) {
        List<Lawyer> lawyers = new ArrayList<>();
        try {
            if (speciality == null || speciality.isEmpty()) {
                throw new IllegalArgumentException("La spécialité ne peut pas être vide");
            }
            
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new Exception("Impossible de se connecter à la base de données");
            }
            
            String query = "SELECT * FROM lawyers WHERE speciality = ? AND experience >= ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, speciality);
            pstmt.setInt(2, minExperience);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                lawyers.add(new Lawyer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("speciality"),
                    rs.getInt("experience"),
                    rs.getDouble("rating"),
                    rs.getString("address")
                ));
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de recherche");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la recherche des avocats : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
        return lawyers;
    }
}
