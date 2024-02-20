package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.connectionFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AjoutRevenuController {
    @FXML
    private TextField montantTextField;

    @FXML
    private DatePicker dateTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private ChoiceBox<String> banqueChoiceBox;

    private Connection connection;

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

    // Méthode pour récupérer les noms des banques depuis la base de données
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

    // Méthode appelée lors de l'action sur le bouton "Valider"
    @FXML
    private void validerAjoutRevenu() {
        // Récupérer les valeurs des champs
        String montant = montantTextField.getText();
        java.sql.Date date = java.sql.Date.valueOf(dateTextField.getValue());
        String description = descriptionTextField.getText();
        String banque = banqueChoiceBox.getValue();

        // Insérer ces valeurs dans la base de données
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO revenus (Montant, DateRevenu, Description, IDBanque) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, montant);
            preparedStatement.setDate(2, date);
            preparedStatement.setString(3, description);

            // Récupérer l'ID de la banque correspondant au nom sélectionné
            int idBanque = getIdBanque(banque);
            if (idBanque == -1) {
                // La banque n'existe pas ou il y a une erreur
                afficherErreur("Erreur", "La banque sélectionnée n'existe pas.");
                return;
            }

            preparedStatement.setInt(4, idBanque);

            // Exécuter la requête
            preparedStatement.executeUpdate();

            // Afficher un message de succès
            afficherInformation("Succès", "Le revenu a été ajouté avec succès.");

            // Fermer la fenêtre
            Stage stage = (Stage) montantTextField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion du revenu dans la base de données : " + e.getMessage());
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout du revenu.");
        }
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

    // Méthode pour afficher une alerte d'erreur
    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte d'information
    private void afficherInformation(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}