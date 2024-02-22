package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class MotDePasseController {

    @FXML
    private VBox ajoutForm;

    @FXML
    private VBox changerForm;

    @FXML
    private PasswordField oldPasswordInput;

    // Méthode appelée lorsqu'on clique sur le bouton "Ajouter"
    @FXML
    private void ajouterMotDePasse() {
        // Logique pour ajouter le mot de passe à la base de données
    }

    // Méthode appelée lorsqu'on clique sur le bouton "Changer"
    @FXML
    private void changerMotDePasse() {
        // Logique pour changer le mot de passe dans la base de données
    }

    // Méthode pour initialiser l'affichage en fonction de la présence d'un mot de passe dans la base de données
    public void initialize() {
        // Logique pour déterminer s'il existe déjà un mot de passe dans la base de données


       /* if (motDePasseExiste) {
            ajoutForm.setVisible(false);
            changerForm.setVisible(true);
        } else {
            ajoutForm.setVisible(true);
            changerForm.setVisible(false);
        }*/
    }
}
