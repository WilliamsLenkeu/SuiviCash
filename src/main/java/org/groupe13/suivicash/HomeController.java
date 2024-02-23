package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.groupe13.suivicash.modele.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeController {

    public VBox MonDashboard;
    public AnchorPane MonInscription;
    public VBox passwordForm;
    public PasswordField passwordField;
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private void initialize() throws SQLException {
        MotDePasse motDePasse= new MotDePasse();
        List<MotDePasse> mots= motDePasse.recupererTousLesMotsDePasse();
        if (!mots.isEmpty()){
            MonDashboard.setManaged(false);
            MonDashboard.setVisible(false);
            MonInscription.setManaged(true);
            MonInscription.setVisible(true);
        }
        // Chargement de la vue Depense par défaut
        loadView("vues/DepenseView.fxml");
        effectuerRepetitionsRevenus();
    }

    // Méthode pour effectuer les répétitions de revenus
    private void effectuerRepetitionsRevenus() throws SQLException {
        Revenus revenus = new Revenus();
        revenus.effectuerRepetitionRevenu();
    }

    @FXML
    private void handleDashboardButtonClick(ActionEvent event) {
        loadDashboard();
    }

    @FXML
    private void handleDepenseButtonClick(ActionEvent event) {
        loadView("vues/DepenseView.fxml");
    }

    @FXML
    private void handleRevenuButtonClick(ActionEvent event) {
        loadView("vues/RevenuView.fxml");
    }


    public void handleSecuriteButtonClick(ActionEvent actionEvent) {
        loadView("vues/MotDePasse.fxml");
    }
    private void loadDashboard() {
        loadView("vues/DashboardView.fxml");
    }

    private void loadView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            contentScrollPane.setContent(root);

            // Accès au contrôleur de la vue incluse
            Object controller = null;
            switch (fxmlFileName) {
                case "vues/DashboardView.fxml":
                    controller = new DashboardController();
                    break;
                case "vues/DepenseView.fxml":
                    controller = new DepenseController();
                    break;
                case "vues/RevenuView.fxml":
                    controller = new RevenuController();
                    break;
                case "vues/MotDePasse.fxml":
                    controller = new MotDePasseController();
                    break;
                default:

                    break;
            }

            if (controller != null) {
                // Set the controller to the loaded FXML
                loader.setController(controller);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                // Mot de passe valide, vous pouvez ajouter votre logique ici
                //afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe validé avec succès.");
                MonDashboard.setManaged(true);
                MonDashboard.setVisible(true);
                MonInscription.setManaged(false);
                MonInscription.setVisible(false);
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

}