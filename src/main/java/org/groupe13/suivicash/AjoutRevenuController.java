package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.groupe13.suivicash.modele.Revenus;

import java.sql.SQLException;
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
        Revenus revenus = new Revenus();
        // Récupérer les valeurs des champs
        double montant = Double.parseDouble(montantTextField.getText());
        java.sql.Date date = java.sql.Date.valueOf(dateTextField.getValue());
        String description = descriptionTextField.getText();
        String banque = banqueChoiceBox.getValue();
        String typeRepetition = typeRepetitionChoiceBox.getValue();
        Integer periodeRepetition = Integer.parseInt(periodeRepetitionTextField.getText());

        revenus.ajouterRevenu(montant, date, description, banque, typeRepetition, periodeRepetition);
    }

    // Méthode pour afficher une alerte d'erreur
    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}