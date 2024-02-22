package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.groupe13.suivicash.modele.connectionFile;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AjoutDepenseController {
    public TextField descriptionField;
    public TextField montantField;
    public DatePicker datePicker;
    public ChoiceBox banqueChoiceBox;
    private Connection connection;
    private  String NomCategorie;
    public AjoutDepenseController(){}

    public AjoutDepenseController(String nom){
        this.NomCategorie= nom;
    }

    // Méthode appelée lors de l'initialisation du contrôleur
    @FXML
    private void initialize() {
        connection = connectionFile.getConnection();
        if (connection == null) {
            afficherErreur("Erreur de connexion", "Impossible de se connecter à la base de données.");
        } else {
            // Récupérer les noms des banques depuis la base de données et les ajouter au ChoiceBox
            List<String> nomsBanques = getNomsBanquesFromDB();
            if (nomsBanques.isEmpty()) {
                afficherErreur("Aucune banque trouvée", "Aucune banque n'est disponible dans la base de données.");
            } else {
                banqueChoiceBox.getItems().addAll(nomsBanques);
            }
        }
    }

    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Méthode pour récupérer l'ID de la banque correspondant au nom sélectionné
    private int getIdBanque(String nomBanque) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT IDBanque FROM banques WHERE NomBanque = ?");
        preparedStatement.setString(1, nomBanque);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("IDBanque");
        } else {
            return -1; // Indiquer qu'aucune banque n'a été trouvée
        }
    }

    static int getIdCategorieByNom(String categorieNom, Connection connection) throws SQLException {
        int idCategorie = -1;

        String sql = "SELECT `IDCategorie` FROM `categories` WHERE `NomCategorie` = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, MaSuperGlobale.NomCategorie);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idCategorie = resultSet.getInt("IDCategorie");
            }
        }

        return idCategorie;
    }



    public void HandleAjouterDepense(ActionEvent actionEvent) {
        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String montantStr = montantField.getText();
        String description = descriptionField.getText(); // La description peut être vide

        // Vérifier si le montant est un nombre valide
        if (!isMontantValide(montantStr)) {
            afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Veuillez entrer un montant valide.");
            return;
        }

        // Vérifier si tous les champs obligatoires sont renseignés
        if (date != null && !montantStr.isEmpty()) {
            try {
                // Vérifier si une banque est sélectionnée
                if (banqueChoiceBox.getSelectionModel().isEmpty()) {
                    afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Veuillez choisir une banque.");
                    return; // Sortir de la méthode si la banque n'est pas sélectionnée
                }

                // Récupérer l'ID de la banque sélectionnée
                String nomBanque = banqueChoiceBox.getSelectionModel().getSelectedItem().toString();
                int idBanque = getIdBanque(nomBanque);

                // Récupérer l'ID de la catégorie
                int idCategorie = getIdCategorieByNom(NomCategorie, connection);

                // Convertir le montant en double
                double montant = Double.parseDouble(montantStr);

                // Vérifier si le solde de la banque est suffisant
                double soldeBanque = getSoldeBanque(idBanque);
                if (soldeBanque < montant) {
                    afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Solde insuffisant dans la banque sélectionnée.");
                    return; // Sortir de la méthode si le solde est insuffisant
                }

                // Insertion dans la base de données
                String sqlInsert = "INSERT INTO depenses (Montant, DateDepense, Description, IDBanque, IDCategorie) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statementInsert = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    statementInsert.setDouble(1, montant);
                    statementInsert.setObject(2, date);
                    statementInsert.setString(3, description);
                    statementInsert.setInt(4, idBanque);
                    statementInsert.setInt(5, idCategorie);

                    // Exécuter la requête d'insertion
                    int rowsAffected = statementInsert.executeUpdate();

                    if (rowsAffected > 0) {
                        // Récupérer l'ID de la dépense insérée
                        ResultSet generatedKeys = statementInsert.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int idDepense = generatedKeys.getInt(1);

                            // Débiter le solde de la banque
                            debiterSoldeBanque(idBanque, montant);

                            // Afficher un message de succès
                            afficherBoiteDialogue(AlertType.INFORMATION, "Succès", "Dépense ajoutée avec succès. Le solde la banque choisie a ete debite avec succes rendez vous dans Banques pour voir le nouveau solde" );
                        } else {
                            afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Échec de l'obtention de l'ID de la dépense.");
                        }
                    } else {
                        afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Échec de l'ajout de la dépense.");
                    }
                }
            } catch (NumberFormatException e) {
                afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Veuillez entrer un montant valide.");
            } catch (SQLException e) {
                afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la dépense dans la base de données : " + e.getMessage());
            }
        } else {
            // Afficher un message d'erreur indiquant que tous les champs obligatoires doivent être remplis
            afficherBoiteDialogue(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
        }
    }

    // Méthode pour vérifier si le montant est un nombre valide
    private boolean isMontantValide(String montantStr) {
        try {
            Double.parseDouble(montantStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Méthode pour débiter le solde de la banque
    private void debiterSoldeBanque(int idBanque, double montant) throws SQLException {
        String sqlUpdate = "UPDATE banques SET Solde = Solde - ? WHERE IDBanque = ?";
        try (PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate)) {
            statementUpdate.setDouble(1, montant);
            statementUpdate.setInt(2, idBanque);
            statementUpdate.executeUpdate();
        }
    }

    // Méthode pour obtenir le solde de la banque
    private double getSoldeBanque(int idBanque) throws SQLException {
        double solde = 0.0;
        String sqlSelect = "SELECT Solde FROM banques WHERE IDBanque = ?";
        try (PreparedStatement statementSelect = connection.prepareStatement(sqlSelect)) {
            statementSelect.setInt(1, idBanque);
            ResultSet resultSet = statementSelect.executeQuery();
            if (resultSet.next()) {
                solde = resultSet.getDouble("Solde");
            }
        }
        return solde;
    }

    private void afficherBoiteDialogue(AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private List<String> getNomsBanquesFromDB() {
        List<String> nomsBanques = new ArrayList<>();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT NomBanque FROM banques");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                nomsBanques.add(nomBanque);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des banques depuis la base de données : " + e.getMessage());
        }
        return nomsBanques;
    }

}
