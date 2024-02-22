package org.groupe13.suivicash;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import  org.groupe13.suivicash.modele.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.groupe13.suivicash.AjoutDepenseController.getIdCategorieByNom;

public class ListDepenseController {

    private  connectionFile connectionFile;
    public Button AjouterDepenseButton;
    @FXML
    private TableColumn<Depense, Button> deleteCol;
    private String categorieSelectionnee;
    private List<Depense> MesDep;

    Stage stage;
    private String NomCategorie;

    @FXML
    private TableView<Depense> depenseTableView;

    @FXML
    private TableColumn<Depense, Double> montantCol;
    @FXML
    private TableColumn<Depense, Date> dateCol;
    @FXML
    private TableColumn<Depense, String> descriptionCol;
    @FXML
    private TableColumn<Depense, String> idBanqueCol;

    public void initialize() {
        // Initialiser les colonnes avec les propriétés de la classe Depense

        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateDepense"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        idBanqueCol.setCellValueFactory(new PropertyValueFactory<>("nomBanque"));

        deleteCol.setCellValueFactory(param -> {
            Button deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(event -> handleDeleteDepense(param.getValue()));
            return new SimpleObjectProperty<>(deleteButton);
        });



        Depense depense= new Depense();
        MesDep= depense.getDepensesByCategorie(MaSuperGlobale.NomCategorie);
        setDepenses(MesDep);

    }

    private void handleDeleteDepense(Depense depense) {
        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Si vous supprimez cette dépense, le solde de votre banque sera restauré. Êtes-vous sûr de vouloir continuer?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // L'utilisateur a cliqué sur OK, supprimer la ligne de la table depense correspondante
            depenseTableView.getItems().remove(depense);

            // Supprimer la dépense de la base de données
            depense.deleteDepense();

            // Restaurer le solde de la banque
            int idBanque = depense.getIdBanque();
            double montant = depense.getMontant();
            crediterSoldeBanque(idBanque, montant);
        }
        // Sinon, ne rien faire
    }


    public void setDepenses(List<Depense> depenses) {
        // Vérifier si depenseTableView est initialisé
        if (depenses != null) {
            // Create a new list to hold the updated depenses with bank names
            List<Depense> updatedDepenses = new ArrayList<>();

            // Iterate over the depenses and retrieve the bank name for each expense
            for (Depense depense : depenses) {
                int idBanque = depense.getIdBanque();
                String bankName = getBankNameById(idBanque); // Implement this method to retrieve the bank name based on the ID



                // Add the updated expense to the list
                updatedDepenses.add(depense);
            }

            // Update the data in the TableView
            depenseTableView.getItems().setAll(updatedDepenses);
        }
    }

    // Méthode pour obtenir le nom de la banque à partir de l'ID
    public String getBankNameById(int idBanque) {
        String bankName = null;
        String query = "SELECT NomBanque FROM banques WHERE IDBanque = ?";

        try (Connection connection = connectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idBanque);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bankName = resultSet.getString("NomBanque");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon vos besoins
        }

        return bankName;
    }


    public ListDepenseController(){}

    public ListDepenseController(List<Depense> Maliste, String NomCategorie){

        Depense depense= new Depense();
        Maliste=depense.getDepensesByCategorie(MaSuperGlobale.NomCategorie);

    }

    public void handleAjouterDepenseClick(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutDepense.fxml"));

            // Créer une instance du contrôleur avec le nom de la catégorie en paramètre
            AjoutDepenseController ajoutDepenseController = new AjoutDepenseController(this.NomCategorie);

            // Définir le contrôleur personnalisé pour le FXMLLoader
            loader.setController(ajoutDepenseController);

            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.setTitle("Ajouter Dépenses");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la fenêtre
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void crediterSoldeBanque(int idBanque, double montant) {
        try {
            Connection connection = connectionFile.getConnection();
            String sql = "UPDATE banques SET Solde = Solde + ? WHERE IDBanque = ?";

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
        } catch (SQLException e) {
            afficherBoiteDialogue(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du solde de la banque : " + e.getMessage());
        }
    }

    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
}