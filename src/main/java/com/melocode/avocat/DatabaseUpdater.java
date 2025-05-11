package com.melocode.avocat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseUpdater {
    public static void updateDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Exécuter le script d'initialisation
            executeSqlScript(stmt, "/database_init.sql");
            
            // Exécuter le script de mise à jour
            executeSqlScript(stmt, "/database_update.sql");
            
            // Afficher les utilisateurs existants
            displayExistingUsers(conn);
            
            System.out.println("Base de données mise à jour avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void executeSqlScript(Statement stmt, String scriptPath) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(DatabaseUpdater.class.getResourceAsStream(scriptPath)))) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires
                if (!line.trim().startsWith("--")) {
                    sql.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0);
                    }
                }
            }
        }
    }

    private static void displayExistingUsers(Connection conn) {
        try {
            System.out.println("\nUtilisateurs existants dans la base de données :");
            System.out.println("----------------------------------------");
            ResultSet rs = conn.createStatement().executeQuery("SELECT email, password, name FROM users");
            while (rs.next()) {
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Mot de passe: " + rs.getString("password"));
                System.out.println("Nom: " + rs.getString("name"));
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 