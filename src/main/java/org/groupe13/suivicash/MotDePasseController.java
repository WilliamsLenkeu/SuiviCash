package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import org.groupe13.suivicash.modele.MotDePasse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class MotDePasseController {

    public PasswordField confirmPasswordInput;
    public PasswordField newPasswordInput;
    @FXML
    private VBox ajoutForm;

    @FXML
    private VBox changerForm;

    @FXML
    private PasswordField oldPasswordInput;

    // Méthode appelée lorsqu'on clique sur le bouton "Ajouter"
    @FXML
    private void ajouterMotDePasse() {
        // Récupérer les valeurs des champs de mot de passe depuis les contrôles FXML
        PasswordField newPasswordInput = (PasswordField) ajoutForm.getChildren().get(1);
        PasswordField confirmPasswordInput = (PasswordField) ajoutForm.getChildren().get(2);

        // Valider si les champs du nouveau mot de passe ne sont pas vides
        String newPassword = newPasswordInput.getText();
        String confirmPassword = confirmPasswordInput.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs du nouveau mot de passe.");
            return;
        }

        // Valider si les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        // Créer une instance de MotDePasse
        MotDePasse motDePasse = new MotDePasse();

        // Définir le nouveau mot de passe
        motDePasse.setMotDePasse(newPassword);

        // Appeler la méthode pour insérer le mot de passe dans la base de données
        if (motDePasse.insererMotDePasse()) {
            // Mettre à jour l'affichage après l'ajout du mot de passe
            ajoutForm.setVisible(false);
            ajoutForm.setManaged(false);
            changerForm.setVisible(true);
            changerForm.setManaged(true);
        } else {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du mot de passe.");
        }
    }

    // Méthode appelée lorsqu'on clique sur le bouton "Changer"
    @FXML
    private void changerMotDePasse() {
        // Récupérer les valeurs des champs de mot de passe depuis les contrôles FXML
        PasswordField oldPasswordInput = (PasswordField) changerForm.getChildren().get(1);
        PasswordField newPasswordInput = (PasswordField) changerForm.getChildren().get(2);
        PasswordField confirmPasswordInput = (PasswordField) changerForm.getChildren().get(3);

        String oldPassword = oldPasswordInput.getText();
        String newPassword = newPasswordInput.getText();
        String confirmPassword = confirmPasswordInput.getText();

        // Vérifier si les nouveaux mots de passe sont vides
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs du nouveau mot de passe.");
            return;
        }
        // Vérifier si les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        // Créer une instance de MotDePasse
        MotDePasse motDePasse = new MotDePasse();
        List<MotDePasse> Mot = motDePasse.recupererTousLesMotsDePasse();
        motDePasse.setIdUtilisateur(Mot.get(0).getIdUtilisateur());

        motDePasse.setMotDePasse(newPassword);

        // Appeler la méthode pour modifier le mot de passe dans la base de données
        motDePasse.modifierMotDePasse();

        //afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe modifié avec succès.");
    }


    @FXML

    private void SupprimerMotDePasse() {
        // Récupérer les valeurs des champs de mot de passe depuis les contrôles FXML
        PasswordField oldPasswordInput = (PasswordField) changerForm.getChildren().get(1);

        // Vérifier si oldPasswordInput n'est pas null
        if (oldPasswordInput == null) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération de l'ancien mot de passe.");
            return;
        }

        // Récupérer l'ancien mot de passe depuis le champ oldPasswordInput
        String oldPassword = oldPasswordInput.getText();

        // Créer une instance de MotDePasse
        MotDePasse motDePasse = new MotDePasse();
        List<MotDePasse> Mots = motDePasse.recupererTousLesMotsDePasse();

        // Vérifier si la liste des mots de passe n'est pas vide
        if (!Mots.isEmpty()) {
            // Récupérer le mot de passe stocké dans la base de données
            String motDePasseStocke = Mots.get(0).getMotDePasse();

            // Vérifier si l'ancien mot de passe correspond au mot de passe stocké
            if (BCrypt.checkpw(oldPassword, motDePasseStocke)) {
                // Si la correspondance est correcte, supprimer le mot de passe
                motDePasse.setIdUtilisateur(Mots.get(0).getIdUtilisateur());
                motDePasse.supprimerMotDePasse();
                // Mettre à jour l'affichage après l'ajout du mot de passe
                ajoutForm.setVisible(true);
                ajoutForm.setManaged(true);
                changerForm.setVisible(false);
                changerForm.setManaged(false);
                // Afficher un message de succès
               // afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe supprimé avec succès.");
            } else {
                // Si l'ancien mot de passe ne correspond pas, afficher un message d'erreur
                afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "L'ancien mot de passe est incorrect.");
            }
        } else {
            // Si la liste des mots de passe est vide, afficher un message d'erreur
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

    // Méthode pour initialiser l'affichage en fonction de la présence d'un mot de passe dans la base de données
    public void initialize() {
        // Logique pour déterminer s'il existe déjà un mot de passe dans la base de données
        MotDePasse  motDePasse = new MotDePasse();
        List<MotDePasse> Mots= motDePasse.recupererTousLesMotsDePasse();

        if (!Mots.isEmpty()) {
            ajoutForm.setVisible(false);
            ajoutForm.setManaged(false);
            changerForm.setVisible(true);
            changerForm.setManaged(true);
        } else {
            ajoutForm.setVisible(true);
            ajoutForm.setManaged(true);
            changerForm.setVisible(false);
            changerForm.setManaged(false);
        }
    }



}
