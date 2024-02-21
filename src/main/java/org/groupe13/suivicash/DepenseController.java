package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.groupe13.suivicash.modele.connectionFile.*;

public class DepenseController {
    public ListView CategorieListView;


    @FXML
    private void initialize() {
        // Récupérer la liste des catégories
        List<String> categories = recuperation();

        // Ajouter les catégories à la ListView en tant que boutons cliquables
        for (String category : categories) {
            Button categoryButton = new Button(category);
            categoryButton.setOnAction(event -> handleCategoryButtonClick(category));
            CategorieListView.getItems().add(categoryButton);
        }
    }

    private ListDepenseController listDepenseController;

    // ...

    public void handleCategorieSelection(ActionEvent event) {
        // Obtenez la catégorie sélectionnée, par exemple, à partir de la ListView
        String categorieSelectionnee = CategorieListView.getSelectionModel().getSelectedItem().toString();

        // Appelez la méthode pour afficher la liste des dépenses
        listDepenseController.afficherListeDepenses(categorieSelectionnee);
    }

    private void handleCategoryButtonClick(String categoryName) {
        // Implémentez le code pour gérer le clic sur un bouton de catégorie
        // Par exemple, vous pourriez afficher les dépenses de cette catégorie.
        afficherBoiteDialogueInformation("Afficher les dépenses pour la catégorie : " , categoryName);
    }

    private void  afficherBoiteDialogueInformation(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();

    }
    public List<String> recuperation(){
        List<String> categories = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Établir la connexion à la base de données
            connection = getConnection();

            // Requête SQL pour récupérer toutes les catégories
            String sql = "SELECT NomCategorie FROM categories";

            // Préparer la déclaration
            preparedStatement = connection.prepareStatement(sql);

            // Exécuter la requête
            resultSet = preparedStatement.executeQuery();

            // Parcourir les résultats et ajouter les catégories à la liste
            while (resultSet.next()) {
                String nomCategorie = resultSet.getString("NomCategorie");
                categories.add(nomCategorie);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        } finally {
            // Fermer la connexion, la déclaration et le résultat
            closeConnection(connection);
            closeStatement(preparedStatement);
            closeResultSet(resultSet);
        }

        return categories;
    }

    public void handleAjouterCategorieClick(ActionEvent actionEvent) {

            try {
                // Charger le fichier FXML de la nouvelle fenêtre
                FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjouterCategorie.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Créer une nouvelle fenêtre modale
                Stage stage = new Stage();
                stage.setTitle("Ajouter Catégorie");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);

                // Afficher la fenêtre
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
}
