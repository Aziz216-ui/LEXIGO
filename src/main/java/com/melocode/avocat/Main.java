package com.melocode.avocat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser la base de données
            System.out.println("Initialisation de la base de données...");
            DatabaseConnection.initializeDatabase();
            
            // Mettre à jour la structure de la base de données
            System.out.println("Mise à jour de la structure de la base de données...");
            DatabaseUpdater.updateDatabase();
            
            // Charger l'interface d'action principale (premier écran)
            System.out.println("Chargement de l'interface d'action principale...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/action.fxml"));
            Parent root = loader.load();

            // Configurer la fenêtre principale
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Système de Gestion d'Avocats");
            
            // Définir une taille minimale pour la fenêtre
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            
            primaryStage.setResizable(false);
            primaryStage.setWidth(900);
            primaryStage.setHeight(600);
            primaryStage.show();
            
            System.out.println("Application démarrée avec succès !");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}