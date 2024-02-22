package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import org.groupe13.suivicash.modele.*;

import java.io.IOException;

public class HomeController {

    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private void initialize() {
        // Chargement de la vue Depense par défaut
        loadView("vues/DepenseView.fxml");

        // Appel de la méthode pour effectuer les répétitions de revenus
        effectuerRepetitionRevenu();
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

    // Méthode pour effectuer les répétitions de revenus
    private void effectuerRepetitionRevenu() {
        Revenus revenus = new Revenus();
        revenus.effectuerRepetitionRevenu();
    }
}