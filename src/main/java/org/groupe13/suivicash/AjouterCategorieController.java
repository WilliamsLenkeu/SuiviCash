package org.groupe13.suivicash;
import org.groupe13.suivicash.modele.connectionFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Stage;

import java.sql.Connection;

public class AjouterCategorieController {

    private Stage stage;
    // Instancier la classe connectionFile
    private connectionFile databaseConnection = new connectionFile();


    // Méthode pour définir le stage à partir de la classe principale
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField nomCategorieTextField;

    public void handleAjouterClick(ActionEvent actionEvent) {
        // Récupérer le texte du champ de texte
        String nomCategorie = nomCategorieTextField.getText();

        // Traitez l'ajout de la catégorie avec le texte récupéré (nomCategorie)
        // Appeler la méthode ajouterCategorie pour ajouter la catégorie
        databaseConnection.ajouterCategorie(nomCategorie,0);


        // Affichez une boîte de dialogue d'information
        afficherBoiteDialogueInformation("Catégorie ajoutée", "Nom de la catégorie : " + nomCategorie);

    }

    private void  afficherBoiteDialogueInformation(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();

    }

    @FXML
    public void handleAnnulerClick(ActionEvent actionEvent) {
        // Annulez l'effet de flou et fermez la fenêtre
        annulerEffetFlou();
        stage.close();
    }

    // Méthode privée pour annuler l'effet de flou
    private void annulerEffetFlou() {
        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
            stage.getScene().getRoot().setEffect(null);
        }
    }

    // Initialisation du contrôleur
    public  int type;
    @FXML
    private void initialize(){
        // Appliquez l'effet de flou lors de l'ouverture du formulaire
        appliquerEffetFlou();
    }


    // Méthode privée pour appliquer l'effet de flou
    private void appliquerEffetFlou() {
        if (stage != null && stage.getScene() != null && stage.getScene().getRoot() != null) {
            BoxBlur blur = new BoxBlur(5, 5, 3); // Paramètres de flou (ajustez-les selon vos préférences)
            stage.getScene().getRoot().setEffect(blur);
        }
    }
}
