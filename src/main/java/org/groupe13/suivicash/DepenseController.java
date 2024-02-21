package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.Depense;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.groupe13.suivicash.modele.connectionFile.*;

public class DepenseController {
    public ListView CategorieListView;
    public BorderPane Categorie;
    public Button AjouterCategorieButton;

    public  void RendreVisibile(int i){
        if(i==0){
            CategorieListView.setVisible(true);
            CategorieListView.setManaged(true);
            AjouterCategorieButton.setVisible(true);
            AjouterCategorieButton.setManaged(true);
            Categorie.setVisible(false);
            Categorie.setManaged(false);
        }else{
            CategorieListView.setVisible(false);
            CategorieListView.setManaged(false);
            AjouterCategorieButton.setVisible(false);
            AjouterCategorieButton.setManaged(false);
            Categorie.setVisible(true);
            Categorie.setManaged(true);
        }
    }
    @FXML
    private void initialize() {
        // Récupérer la liste des catégories
        List<String> categories = recuperation();
        RendreVisibile(0);
        // Ajouter les catégories à la ListView en tant que boutons cliquables
        for (String category : categories) {
            Button categoryButton = new Button(category);
            categoryButton.setOnAction(event -> handleCategoryButtonClick(category));
            CategorieListView.getItems().add(categoryButton);
        }
    }

    private void chargerNouveauContenuAvecInfos(String nom) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/ListDepense.fxml"));
            AnchorPane newContent = loader.load();
            Depense depense= new Depense();

            // Accéder au contrôleur du nouveau contenu
            ListDepenseController nouveauContenuController= new ListDepenseController(depense.getDepensesByCategorie(nom),nom);
            nouveauContenuController  = loader.getController();

            // Remplacer le contenu actuel par le nouveau contenu
            Categorie.setCenter(newContent);
        } catch (Exception e) {
            e.printStackTrace(); // Gérer les exceptions selon votre besoin
        }
    }


    private void handleCategoryButtonClick(String categoryName) {
        MaSuperGlobale.NomCategorie= categoryName;
        System.out.println(categoryName);
        chargerNouveauContenuAvecInfos(categoryName);
        RendreVisibile(1);

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
