package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.groupe13.suivicash.modele.Revenus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class AjoutRevenuController {
    @FXML
    private TextField montantTextField;

    @FXML
    private DatePicker dateTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private ChoiceBox<String> banqueChoiceBox;

    @FXML
    private ChoiceBox<String> typeRepetitionChoiceBox;

    @FXML
    private TextField periodeRepetitionTextField;

    // Méthode appelée lors de l'initialisation du contrôleur
    @FXML
    private void initialize() {
        Revenus revenus = new Revenus();
        List<String> nomsBanques = revenus.getNomsBanques();
        if (nomsBanques.isEmpty()) {
            afficherErreur("Aucune banque trouvée", "Aucune banque n'est disponible dans la base de données.");
        } else {
            banqueChoiceBox.getItems().addAll(nomsBanques);
        }
    }

    // Méthode appelée lors de l'action sur le bouton "Valider"
    @FXML
    private void validerAjoutRevenu() {
        String montant = montantTextField.getText();
        String date = dateTextField.getEditor().getText();
        String description = descriptionTextField.getText();
        String banque = banqueChoiceBox.getValue();
        String typeRepetition = typeRepetitionChoiceBox.getValue();
        String periodeRepetition = periodeRepetitionTextField.getText();

        if (!validateInput(montant, date, description, banque, typeRepetition, periodeRepetition)) {
            return;
        }

        Revenus revenus = new Revenus();
        try {
            double montantValue = Double.parseDouble(montant);
            LocalDate dateValue = dateTextField.getValue();
            int periodeValue = Integer.parseInt(periodeRepetition);
            revenus.ajouterRevenu(montantValue, java.sql.Date.valueOf(dateValue), description, banque, typeRepetition, periodeValue);
        } catch (NumberFormatException e) {
            afficherErreur("Erreur de saisie", "Veuillez saisir des valeurs valides pour le montant et la période de répétition.");
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

    // Méthode pour valider les entrées utilisateur
    private boolean validateInput(String montant, String date, String description, String banque, String typeRepetition, String periodeRepetition) {
        if (montant.isEmpty() || date.isEmpty() || description.isEmpty() || banque == null || typeRepetition == null) {
            afficherErreur("Champs non remplis", "Veuillez remplir tous les champs.");
            return false;
        }

        if (!Pattern.matches("\\d+(\\.\\d+)?", montant)) {
            afficherErreur("Erreur de saisie", "Le montant doit être un nombre valide.");
            return false;
        }

        if (!typeRepetition.equals("unique") && periodeRepetition.isEmpty()) {
            afficherErreur("Champ manquant", "Veuillez spécifier une période de répétition.");
            return false;
        }

        if (!Pattern.matches("\\d+", periodeRepetition)) {
            afficherErreur("Erreur de saisie", "La période de répétition doit être un entier positif.");
            return false;
        }
        if (!periodeRepetition.isEmpty() && !periodeRepetition.matches("\\d+")) {
            afficherErreur("Erreur de saisie", "La période de répétition doit être un entier positif.");
            return false;
        }

        return true;
    }

    // Méthode appelée lors du changement du type de répétition
    @FXML
    private void checkRepetitionType() {
        String typeRepetition = typeRepetitionChoiceBox.getValue();
        periodeRepetitionTextField.setDisable(typeRepetition.equals("unique"));
    }
}