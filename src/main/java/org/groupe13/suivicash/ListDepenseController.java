package org.groupe13.suivicash;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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
// Mettez à jour les références à 'nomBanque' avec 'nomBanque3'
        idBanqueCol.setCellValueFactory(new PropertyValueFactory<>("nomBanque"));


        deleteCol.setCellValueFactory(param -> {
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(event -> handleDeleteDepense(param.getValue()));
            return new SimpleObjectProperty<>(deleteButton);
        });



        Depense depense= new Depense();
        MesDep= depense.getDepensesByCategorie(MaSuperGlobale.NomCategorie);
        setDepenses(MesDep);

    }

    private void handleDeleteDepense(Depense depense) {
        // Perform the delete operation for the selected expense
        // You can use depense.getIdDepense() to get the ID of the expense and delete it from the database

        // Refresh the depenseTableView by removing the deleted expense
        depenseTableView.getItems().remove(depense);
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

        // Imprime les informations de chaque dépense
        for (Depense d : Maliste) {
            System.out.println("ID: " + d.getIdDepense());
            System.out.println("Montant: " + d.getMontant());
            System.out.println("Date: " + d.getDateDepense());
            System.out.println("Description: " + d.getDescription());
            System.out.println("ID Banque: " + d.getnomBanque());

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
/*idBanqueCol.setCellValueFactory(new PropertyValueFactory<>("idBanque"));
idBanqueCol.setCellFactory(new Callback<>() {
    @Override
    public TableCell<Depense, String> call(TableColumn<Depense, String> param) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String idBanque, boolean empty) {
                super.updateItem(idBanque, empty);

                if (empty || idBanque == null) {
                    setText(null);
                } else {
                    // Convertir la chaîne en entier
                    int idBanqueInt = Integer.parseInt(idBanque);

                    // Obtenir le nom de la banque en fonction de l'ID
                    String bankName = getBankNameById(idBanqueInt);

                    setText(bankName);
                }
            }
        };
    }
});

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
}*/