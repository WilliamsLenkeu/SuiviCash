package org.groupe13.suivicash;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import org.groupe13.suivicash.modele.*;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class RevenuController {
    @FXML
    private Text revenuText;

    private Stage stage;

    @FXML
    private TableView<Revenus> revenusTableView;
    @FXML
    private TableColumn<Revenus, Double> montantCol;
    @FXML
    private TableColumn<Revenus, Date> dateCol;
    @FXML
    private TableColumn<Revenus, String> descriptionCol;
    @FXML
    private TableColumn<Revenus, String> nomBanqueCol;

    public void initialize() {
        // Initialiser les colonnes avec les propriétés de la classe Revenus
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant")); // Modifier la propriété "Montant" en minuscules
        dateCol.setCellValueFactory(new PropertyValueFactory<>("DateRevenu"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description")); // Modifier la propriété "Description" en minuscules
        nomBanqueCol.setCellValueFactory(new PropertyValueFactory<>("nomBanque")); // Modifier la propriété "NomBanque" en minuscules

        Revenus revenus = new Revenus();
        List<Revenus> listeRevenus = revenus.getRevenus();
        setRevenus(listeRevenus);
    }

    public void setRevenus(List<Revenus> revenus) {
        // Vérifier si revenusTableView est initialisé
        if (revenusTableView != null) {
            // Vérifier si revenus est initialisé
            if (revenus != null) {
                // Mettre à jour les données dans la TableView
                revenusTableView.getItems().setAll(revenus);
            } else {
                System.err.println("Erreur : La liste de revenus est null.");
            }
        } else {
            System.err.println("Erreur : TableView revenusTableView non initialisée.");
        }
    }

    @FXML
    private void handleAjouterRevenuClick() {
        try {
            // Charger le fichier FXML de la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutRevenu.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.setTitle("Ajouter Revenu");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la fenêtre
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}