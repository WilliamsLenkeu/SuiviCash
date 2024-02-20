package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.groupe13.suivicash.modele.connectionFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AjoutBanqueController {

    @FXML
    private TextField nomBanqueField;

    @FXML
    private TextField soldeField;

    private Connection connection;

    @FXML
    private void handleAjouterBanque() {
        String nomBanque = nomBanqueField.getText();
        String soldeText = soldeField.getText();

        // Vérifier que les champs ne sont pas vides
        if (nomBanque.isEmpty() || soldeText.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        // Convertir le solde en double
        double solde;
        try {
            solde = Double.parseDouble(soldeText);
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un solde valide.");
            return;
        }

        // Se reconnecter à la base de données
        connection = connectionFile.getConnection();

        // Insérer la nouvelle banque dans la base de données
        try {
            String query = "INSERT INTO banques (NomBanque, Solde) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nomBanque);
            preparedStatement.setDouble(2, solde);
            preparedStatement.executeUpdate();
            showAlert("Banque ajoutée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ajout de la banque.");
        } finally {
            // Fermer la connexion à la base de données
            connectionFile.closeConnection(connection);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}