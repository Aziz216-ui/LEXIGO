package com.melocode.avocat;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class EmailService {
    private static final String FROM_EMAIL = "melkimohamedaziz1@gmail.com";
    private static final String APP_PASSWORD = "oeytaxvsbjoupjrn"; // Mot de passe sans espaces
    private static final String TO_EMAIL = "melkimohamedaziz1@gmail.com";

    public static void sendConfirmationEmail(String lawyerName, String date) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                        }
                    });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(FROM_EMAIL));
                message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(TO_EMAIL));
                message.setSubject("✅ Confirmation Rendez-vous Avocat - " + lawyerName);

                String htmlContent = "<h3 style='color:#2c3e50;'>Bonjour,</h3>"
                    + "<p>Votre rendez-vous avec Maître <strong>" + lawyerName + "</strong> est confirmé !</p>"
                    + "<p><strong>📅 Date :</strong> " + date + "</p>"
                    + "<p style='margin-top:20px;'>Cordialement,<br/>Équipe Juridique</p>"
                    + "<hr style='border-color:#3498db;'>"
                    + "<small style='color:#7f8c8d;'>Cet email est généré automatiquement, merci de ne pas y répondre.</small>";

                message.setContent(htmlContent, "text/html; charset=utf-8");

                Transport.send(message);
                showSuccessAlert("✔️ Email envoyé avec succès !");
            } catch (Exception e) {
                showErrorAlert("❌ Échec d'envoi : " + e.getMessage());
            }
        }).start();
    }

    private static void showSuccessAlert(String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Confirmation Email");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private static void showErrorAlert(String error) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur Email");
            alert.setHeaderText(null);
            alert.setContentText(error);
            alert.showAndWait();
        });
    }
}