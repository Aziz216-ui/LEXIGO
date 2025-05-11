package com.melocode.avocat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/avocat_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void initializeDatabase() {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL chargé avec succès !");
            
            // Tester la connexion
            try (Connection conn = getConnection()) {
                System.out.println("Connexion à la base de données réussie !");
                System.out.println("URL de connexion : " + DB_URL);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Impossible de charger le driver MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur : Impossible de se connecter à la base de données.");
            System.err.println("Message d'erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Nouvelle connexion établie avec succès !");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données :");
            System.err.println("URL : " + DB_URL);
            System.err.println("Utilisateur : " + USER);
            System.err.println("Message d'erreur : " + e.getMessage());
            throw e;
        }
    }
}
