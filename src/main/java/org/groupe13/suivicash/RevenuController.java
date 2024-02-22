package org.groupe13.suivicash;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import  org.groupe13.suivicash.modele.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @FXML
    private TableColumn<Revenus, Button> deleteCol;

    public void initialize() {
        // Initialiser les colonnes avec les propriétés de la classe Revenus
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("DateRevenu"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        nomBanqueCol.setCellValueFactory(new PropertyValueFactory<>("nomBanque"));

        deleteCol.setCellValueFactory(param -> {
            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(event -> {
                handleDeleteRevenus(param.getValue());
            });
            return new SimpleObjectProperty<>(deleteButton);
        });

        Revenus revenus = new Revenus();
        List<Revenus> listeRevenus = revenus.getRevenus();
        setRevenus(listeRevenus);
    }

    private void handleDeleteRevenus(Revenus revenus) {
        try {
            // Afficher une boîte de dialogue de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Si vous supprimez cette dépense, le solde de votre banque sera restauré. Êtes-vous sûr de vouloir continuer?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // L'utilisateur a cliqué sur OK, supprimer la ligne de la table revenus correspondante
                revenusTableView.getItems().remove(revenus);

                // Supprimer le revenu de la base de données
                revenus.deleteRevenus();

                // débiter le solde de la banque
                int idBanque = revenus.getIdBanque(revenus.getNomBanque());
                double montant = revenus.getMontant();
                debiterSoldeBanque(idBanque, montant);
            }
            // Sinon, ne rien faire
        } catch (SQLException e) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du revenu : " + e.getMessage());
        }
    }

    public void debiterSoldeBanque(int idBanque, double montant) throws SQLException {
        try (Connection connection = connectionFile.getConnection()) {
            String sql = "UPDATE banques SET Solde = Solde - ? WHERE IDBanque = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, montant);
                statement.setInt(2, idBanque);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Solde de la banque mis à jour avec succès.");
                } else {
                    afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du solde de la banque.");
                }
            }
        }
    }

    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
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