package org.groupe13.suivicash;
import  org.groupe13.suivicash.modele.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.List;

public class AjoutLimiteController {

    @FXML
    private TextField limiteField;
   private List<LimiteDepense> limites;

    public void initialize() {
        // Initialiser la classe LimiteDepense
        LimiteDepense limiteDepense = new LimiteDepense();

        // Récupérer la liste des limites depuis la base de données
         limites = limiteDepense.getLimites();

        // Vérifier si des limites existent
        if (!((List<?>) limites).isEmpty()) {
            // Récupérer la première limite de la liste (par exemple)
            LimiteDepense premiereLimite = limites.get(0);

            // Afficher la limite existante dans le champ de texte
            limiteField.setText(String.valueOf(premiereLimite.getLimite()));
        }
    }
    public void handleAjouterLimite(ActionEvent actionEvent) {
        // Récupérer la valeur du champ de limite
        String limiteStr = limiteField.getText();

        // Vérifier si la limite est un nombre valide
        if (!isLimiteValide(limiteStr)) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une limite valide.");
            return;
        }

        // Vérifier si des limites existent
        if (!limites.isEmpty()) {
            // Récupérer la première limite de la liste (par exemple)
            LimiteDepense premiereLimite = limites.get(0);

            premiereLimite.mettreAJourLimiteDepense(premiereLimite.getId(), Double.parseDouble(limiteStr));

            // Afficher un message de succès
            afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Limite de dépense mise à jour avec succès.");
            return;
        }


        // Ajouter la limite de dépense
        double limiteMensuelle = Double.parseDouble(limiteStr);
        LimiteDepense limiteDepense = new LimiteDepense();
        limiteDepense.ajouterLimiteDepense(limiteMensuelle);


        // Afficher un message de succès
        afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Limite de dépense ajoutée avec succès.");
    }

    private boolean isLimiteValide(String limiteStr) {
        try {
            double limite = Double.parseDouble(limiteStr);
            return limite >= 0; // La limite doit être un nombre positif
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
