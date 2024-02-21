package org.groupe13.suivicash;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import  org.groupe13.suivicash.modele.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

import static org.groupe13.suivicash.AjoutDepenseController.getIdCategorieByNom;

public class ListDepenseController {

    public Button AjouterDepenseButton;
    private String categorieSelectionnee;
    private List<Depense> MesDep;

    Stage stage;
    private String NomCategorie;

    @FXML
    private TableView<Depense> depenseTableView;
    @FXML
    private TableColumn<Depense, Integer> idCol;
    @FXML
    private TableColumn<Depense, Double> montantCol;
    @FXML
    private TableColumn<Depense, Date> dateCol;
    @FXML
    private TableColumn<Depense, String> descriptionCol;
    @FXML
    private TableColumn<Depense, Integer> idBanqueCol;
    @FXML
    private TableColumn<Depense, Integer> idCategorieCol;

    public void initialize() {
        // Initialiser les colonnes avec les propriétés de la classe Depense
        idCol.setCellValueFactory(new PropertyValueFactory<>("idDepense"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateDepense"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        idBanqueCol.setCellValueFactory(new PropertyValueFactory<>("idBanque"));
        idCategorieCol.setCellValueFactory(new PropertyValueFactory<>("idCategorie"));
        Depense depense= new Depense();
        MesDep= depense.getDepensesByCategorie(MaSuperGlobale.NomCategorie);
        setDepenses(MesDep);

    }

    public void setDepenses(List<Depense> depenses) {
        // Vérifier si depenseTableView est initialisé
        if (depenseTableView != null) {
            // Vérifier si depenses est initialisé
            if (depenses != null) {
                // Mettre à jour les données dans la TableView
                depenseTableView.getItems().setAll(depenses);
            } else {
                System.err.println("Erreur : La liste de dépenses (depenses) est null.");
                // Gérer le cas où depenses est null (selon vos besoins)
            }
        } else {
            System.err.println("Erreur : TableView (depenseTableView) non initialisée.");
            // Gérer le cas où depenseTableView est null (selon vos besoins)
        }
    }


    public ListDepenseController(){}

    public ListDepenseController(List<Depense> Maliste, String NomCategorie){

        Depense depense= new Depense();
        Maliste=depense.getDepensesByCategorie(MaSuperGlobale.NomCategorie);

        // Imprime les informations de chaque dépense
        for (Depense d : Maliste) {
            System.out.println("ID: " + d.getIdDepense());
            System.out.println("Montant: " + d.getMontant());
            System.out.println("Date: " + d.getDateDepense());
            System.out.println("Description: " + d.getDescription());
            System.out.println("ID Banque: " + d.getIdBanque());
            System.out.println("ID Catégorie: " + d.getIdCategorie());
            System.out.println("=========================");
        }




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




    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
