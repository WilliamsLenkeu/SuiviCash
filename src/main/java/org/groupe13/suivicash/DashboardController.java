package org.groupe13.suivicash;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import org.groupe13.suivicash.modele.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private ListView<Banque> banqueListView;

    @FXML
    private Label totalSoldeLabel;

    @FXML
    private Label totalDepensesLabel;

    @FXML
    private Label totalRevenusLabel;

    @FXML
    private BarChart<String, Number> depensesBarChart;

    @FXML
    private BarChart<String, Number> revenusBarChart;

    private Stage stage;

    private ObservableList<Banque> banques;

    private Connection connection;

    @FXML
    private void initialize() {
        connection = connectionFile.getConnection();
        loadBanquesFromDatabase();
        calculerTotalSolde();
        /*calculerTotalDepenses();
        calculerTotalRevenus();
        afficherDepensesParMois();
        afficherRevenusParMois();*/
    }

    @FXML
    private void handleAjouterBanqueClick() {
        try {
            // Charger le fichier FXML de la nouvelle fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutBanque.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.setTitle("Ajouter Banque");
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

    private void loadBanquesFromDatabase() {
        banques = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT NomBanque, Solde FROM banques");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                double solde = resultSet.getDouble("Solde");
                Banque banque = new Banque(nomBanque, solde);
                banques.add(banque);
            }
            banqueListView.setItems(banques);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculerTotalSolde() {
        double totalSolde = 0;
        for (Banque banque : banques) {
            totalSolde += banque.getSolde();
        }
        totalSoldeLabel.setText(String.format("%.2f", totalSolde));
    }

    private void calculerTotalDepenses() {
        double totalDepenses = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(Montant) AS TotalDepenses FROM depenses");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalDepenses = resultSet.getDouble("TotalDepenses");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        totalDepensesLabel.setText(String.format("%.2f", totalDepenses));
    }

    private void calculerTotalRevenus() {
        double totalRevenus = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(Montant) AS TotalRevenus FROM revenus");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalRevenus = resultSet.getDouble("TotalRevenus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        totalRevenusLabel.setText(String.format("%.2f", totalRevenus));
    }

    private void afficherDepensesParMois() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT Mois, SUM(Montant) AS TotalDepenses FROM depenses GROUP BY Mois");
            ResultSet resultSet = statement.executeQuery();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (resultSet.next()) {
                String mois = resultSet.getString("Mois");
                double totalDepenses = resultSet.getDouble("TotalDepenses");
                series.getData().add(new XYChart.Data<>(mois, totalDepenses));
            }
            depensesBarChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherRevenusParMois() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT Mois, SUM(Montant) AS TotalRevenus FROM revenus GROUP BY Mois");
            ResultSet resultSet = statement.executeQuery();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (resultSet.next()) {
                String mois = resultSet.getString("Mois");
                double totalRevenus = resultSet.getDouble("TotalRevenus");
                series.getData().add(new XYChart.Data<>(mois, totalRevenus));
            }
            revenusBarChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}