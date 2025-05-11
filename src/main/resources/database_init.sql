-- Création de la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS avocat_db;
USE avocat_db;

-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table lawyers (avocats)
CREATE TABLE IF NOT EXISTS lawyers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    speciality VARCHAR(255),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table admins
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identifiant VARCHAR(255)

);

-- Création de la table reservations
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lawyer_id INT NOT NULL,
    client_nom VARCHAR(255) NOT NULL,
    client_prenom VARCHAR(255) NOT NULL,
    client_sexe VARCHAR(10) NOT NULL,
    client_date_naissance DATE NOT NULL,
    date_rdv DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lawyer_id) REFERENCES lawyers(id)
);

-- Suppression des utilisateurs existants pour éviter les doublons
DELETE FROM users;

-- Insertion des utilisateurs
INSERT INTO users (email, password, name) VALUES 
('admin@admin.com', 'admin', 'Administrateur'),
('user@example.com', 'user123', 'Utilisateur Test'),
('avocat@example.com', 'avocat123', 'Avocat Test')
ON DUPLICATE KEY UPDATE email = email;

-- Ajout des avocats s'ils n'existent pas déjà
INSERT INTO lawyers (name, speciality, address) VALUES
('Me. Dubois', 'Droit de la famille', '123 Rue de la Justice, 75001 Paris'),
('Me. Martin', 'Droit pénal', '456 Avenue des Avocats, 75002 Paris'),
('Me. Bernard', 'Droit des affaires', '789 Boulevard du Droit, 75003 Paris')
ON DUPLICATE KEY UPDATE name = name;

