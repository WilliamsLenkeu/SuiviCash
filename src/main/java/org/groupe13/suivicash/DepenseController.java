package org.groupe13.suivicash;

//import com.sun.mail.imap.Rights;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.groupe13.suivicash.modele.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.Depense;
import org.groupe13.suivicash.modele.LimiteDepense;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.groupe13.suivicash.modele.connectionFile.*;

public class DepenseController {
    public ListView CategorieListView;
    public BorderPane Categorie;
    public Button AjouterCategorieButton;
    public VBox MonVbox;
    public Text MaLimite;

    public Button ChangerLimiteButton;
    public HBox AjouterLimite;
    public Button AjouterLimiteButton;
    public VBox LimiteChanger;
    public Button SupprimerLimiteButton;
    public Text Avertissement;
    public Text MonTotal;
    public List<LimiteDepense> limites;
    public ComboBox MonthComboBox;

    public  void RendreVisibile(int i){
        if(i==0){
            MonVbox.setVisible(true);
            MonVbox.setManaged(true);

            // Initialiser la classe LimiteDepense
            LimiteDepense limiteDepense = new LimiteDepense();

            // Récupérer la liste des limites depuis la base de données
            limites = limiteDepense.getLimites();

            // Vérifier si des limites existent
            if (!((List<?>) limites).isEmpty()) {
                AjouterLimite.setVisible(false);
                AjouterLimite.setManaged(false);
                MaLimite.setText(""+limites.get(0).getLimite()+"XAF");
                LimiteChanger.setVisible(true);
                LimiteChanger.setManaged(true);
            }else {
                AjouterLimite.setVisible(true);
                AjouterLimite.setManaged(true);
                LimiteChanger.setVisible(false);
                LimiteChanger.setManaged(false);
            }
            Categorie.setVisible(false);
            Categorie.setManaged(false);
        }else{
            MonVbox.setVisible(false);
            MonVbox.setManaged(false);
            Categorie.setVisible(true);
            Categorie.setManaged(true);
        }
    }
    @FXML
    private void initialize() {

        // Remplir la ComboBox avec les mois
        MonthComboBox.setItems(FXCollections.observableArrayList(
                "Tous", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        ));

        LocalDate currentDate = LocalDate.now();
        String moisActuel = currentDate.format(DateTimeFormatter.ofPattern("MMMM")); // Obtenir le nom du mois
        MonthComboBox.setValue(moisActuel);

        updateListViewWithFilteredData(currentDate.getMonthValue(),currentDate.getYear());


        RendreVisibile(0);
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

    public void handleChangerLimiteClick(ActionEvent actionEvent) {
        handleAjouterLimiteClick(actionEvent);

    }

    public void handleAjouterLimiteClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutLimite.fxml"));
            AnchorPane root = loader.load();

            Stage ajoutLimiteStage = new Stage();
            ajoutLimiteStage.initModality(Modality.APPLICATION_MODAL);
            ajoutLimiteStage.setTitle("Ajout de Limite de Dépense");
            ajoutLimiteStage.setScene(new Scene(root));

            // Afficher la fenêtre d'ajout de limite
            ajoutLimiteStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée dans votre application
        }
    }


    public void handleSupprimerLimiteClick(ActionEvent actionEvent) {
        LimiteDepense limiteDepense = new LimiteDepense();

        // Récupérer la liste des limites depuis la base de données
        List<LimiteDepense> limites = limiteDepense.getLimites();

        // Vérifier si des limites existent
        if (!limites.isEmpty()) {
            // Supprimer la première limite de la liste (par exemple)
            LimiteDepense premiereLimite = limites.get(0);
            limiteDepense.supprimerLimiteDepense(premiereLimite.getId());

            // Afficher un message de succès
            afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Limite de dépense supprimée avec succès.");
        }
    }

    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    @FXML
    private void handleMonthFilterChange(ActionEvent event) {
        String moisSelectionne = (String) MonthComboBox.getValue();
        if(moisSelectionne=="Tous"){
            updateListViewWithFilteredData(-1, -1);
            return;
        }
        // Assurez-vous que les noms des mois en français correspondent aux constantes d'énumération Month
        Month selectedMonth = Month.JANUARY; // Valeur par défaut

        switch (moisSelectionne.toUpperCase()) {
            case "JANVIER":
                selectedMonth = Month.JANUARY;
                break;
            case "FÉVRIER":
                selectedMonth = Month.FEBRUARY;
                break;
            case "MARS":
                selectedMonth = Month.MARCH;
                break;
            case "AVRIL":
                selectedMonth = Month.APRIL;
                break;
            case "MAI":
                selectedMonth = Month.MAY;
                break;
            case "JUIN":
                selectedMonth = Month.JUNE;
                break;
            case "JUILLET":
                selectedMonth = Month.JULY;
                break;
            case "AOÛT":
                selectedMonth = Month.AUGUST;
                break;
            case "SEPTEMBRE":
                selectedMonth = Month.SEPTEMBER;
                break;
            case "OCTOBRE":
                selectedMonth = Month.OCTOBER;
                break;
            case "NOVEMBRE":
                selectedMonth = Month.NOVEMBER;
                break;
            case "DÉCEMBRE":
                selectedMonth = Month.DECEMBER;
                break;

        }

        // Utilisez la constante d'énumération Month dans votre logique
        int numeroMois = selectedMonth.getValue();

        int anneeActuelle = LocalDate.now().getYear();

        // Mettre à jour la liste des catégories en fonction du mois et de l'année
        updateListViewWithFilteredData(numeroMois, anneeActuelle);
    }

    private  void updateListViewWithFilteredData(int numeroMois, int annee) {

        // Récupérer la liste des catégories
        List<Categorie> categories = new Categorie().getAllCategories(annee, numeroMois);

        // Effacer la ListView actuelle
        CategorieListView.getItems().clear();


        double total= 0.0;
        // Ajouter les catégories à la ListView en tant que boutons cliquables
        for (Categorie category : categories) {
            double totalDepense = category.getTotalDepense();
            String displayText = category.getNomCategorie() + "     - Total Dépense : " + totalDepense;
            Button categoryButton = new Button(displayText);
            categoryButton.setStyle("-fx-background-color: #dddddd; -fx-text-fill: black; -fx-font-size: 14px");
            categoryButton.setPrefWidth(400);
            categoryButton.setPrefHeight(40);
            categoryButton.setAlignment(Pos.BOTTOM_LEFT);
            categoryButton.setOnAction(event -> handleCategoryButtonClick(category.getNomCategorie()));
            CategorieListView.getItems().add(categoryButton);
            total+= category.getTotalDepense();
        }
        MonTotal.setText(""+total+"XAF");
        // Initialiser la classe LimiteDepense
        LimiteDepense limiteDepense = new LimiteDepense();

        // Récupérer la liste des limites depuis la base de données
        limites = limiteDepense.getLimites();
        /*Avertissement.setVisible(false);
        Avertissement.setManaged(false);*/
        // Vérifier si des limites existent
        if (!(limites).isEmpty()) {
            if(limites.get(0).getLimite()<total){

                Month month = Month.of(numeroMois);
                String nomMois = getNomMoisFrancais(numeroMois);
                Avertissement.setText("Attention, vous avez dépassé votre limite de dépense pour "+nomMois+" "+LocalDate.now().getYear());
                Avertissement.setStyle("-fx-text-fill: red;");
                Avertissement.setVisible(true);
                Avertissement.setManaged(true);
            }

        }

    }

    // Fonction pour obtenir le nom du mois en français
    private String getNomMoisFrancais(int numeroMois) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.FRENCH);
        String[] nomsMois = dfs.getMonths();
        return nomsMois[numeroMois - 1];
    }
}






