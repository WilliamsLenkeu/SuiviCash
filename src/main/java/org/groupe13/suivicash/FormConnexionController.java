package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.MotDePasse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class FormConnexionController {
    public PasswordField passwordField;

    @FXML
    private void validerMotDePasse() {
        // Récupérer la valeur du champ de mot de passe depuis le contrôle FXML
        String password = passwordField.getText();

        // Valider si le champ du mot de passe n'est pas vide
        if (password.isEmpty()) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un mot de passe.");
            return;
        }

        // Créer une instance de MotDePasse
        MotDePasse motDePasse = new MotDePasse();

        // Récupérer tous les mots de passe de la base de données
        List<MotDePasse> mots = motDePasse.recupererTousLesMotsDePasse();

        // Vérifier si la liste des mots de passe n'est pas vide
        if (!mots.isEmpty()) {
            // Récupérer le mot de passe stocké dans la base de données
            String motDePasseStocke = mots.get(0).getMotDePasse();

            // Vérifier si le mot de passe entré correspond au mot de passe stocké
            if (BCrypt.checkpw(password, motDePasseStocke)) {
                // Mot de passe valide, fermer la fenêtre
                fermerFenetre();
            } else {
                // Mot de passe incorrect
                afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Mot de passe incorrect.");
            }
        } else {
            // Aucun mot de passe trouvé dans la base de données
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Aucun mot de passe trouvé dans la base de données.");
        }
    }

    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.close();
    }
}