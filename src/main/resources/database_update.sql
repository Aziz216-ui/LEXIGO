-- Ajout de la colonne address à la table lawyers
ALTER TABLE lawyers ADD COLUMN address VARCHAR(255);

-- Ajout de la colonne name à la table users si elle n'existe pas déjà
ALTER TABLE users ADD COLUMN name VARCHAR(255);

-- Mise à jour des adresses des avocats existants (exemple)
UPDATE lawyers SET address = '123 Rue de la Justice, 75001 Paris' WHERE id = 1;
UPDATE lawyers SET address = '456 Avenue des Avocats, 75002 Paris' WHERE id = 2;
UPDATE lawyers SET address = '789 Boulevard du Droit, 75003 Paris' WHERE id = 3; 