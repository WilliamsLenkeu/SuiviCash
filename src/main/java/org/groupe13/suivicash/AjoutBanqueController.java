package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.connectionFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;

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

        // Vérifier si la banque existe déjà
        if (banqueExisteDeja(normalizeString(nomBanque))) {
            showAlert("Une banque avec ce nom existe déjà.");
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

            // Fermer la fenêtre après l'ajout
            Stage stage = (Stage) nomBanqueField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ajout de la banque.");
        } finally {
            // Fermer la connexion à la base de données
            connectionFile.closeConnection(connection);
        }
    }

    private boolean banqueExisteDeja(String nomBanque) {
        boolean existe = false;
        try {
            // Assurez-vous que la connexion n'est pas null
            if (connection == null) {
                connection = connectionFile.getConnection();
            }
            String query = "SELECT COUNT(*) FROM banques WHERE LOWER(NOM_BANQUE_NORMALIZED) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nomBanque);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                existe = resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existe;
    }

    private String normalizeString(String input) {
        // Normaliser les caractères pour supprimer les accents
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Convertir en minuscules
        normalized = normalized.toLowerCase();
        return normalized;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}