module com.melocode.avocat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires mysql.connector.java;
    requires java.mail; // Obligatoire pour JavaMail
    requires activation; // Pour les pièces jointes (optionnel)

    exports com.melocode.avocat;
    opens com.melocode.avocat;
}
