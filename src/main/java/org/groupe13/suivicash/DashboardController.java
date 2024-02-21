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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.groupe13.suivicash.modele.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private TableView<Banque> banqueTableView;

    @FXML
    private Label totalSoldeLabel;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private BarChart<String, Number> depensesBarChart;

    @FXML
    private BarChart<String, Number> revenusBarChart;

    private ObservableList<Banque> banques;

    private Connection connection;

    @FXML
    private void initialize() {
        connection = connectionFile.getConnection();
        if (connection != null) {
            fillYearComboBox();
            yearComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateCharts();
                }
            });
            loadBanquesFromDatabase();
            calculerTotalSolde();
        } else {
            // Gérer le cas où la connexion à la base de données a échoué
            System.err.println("La connexion à la base de données a échoué.");
        }
    }

    @FXML
    private void handleAjouterBanqueClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutBanque.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Ajouter Banque");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBanquesFromDatabase() {
        banques = FXCollections.observableArrayList();
        banques.addAll(Banque.getAllBanques(connection));
        banqueTableView.setItems(banques);
    }

    private void calculerTotalSolde() {
        double totalSolde = Banque.getTotalSolde(connection);
        totalSoldeLabel.setText(String.format("%.2f", totalSolde) + " XAF");
    }

    private void fillYearComboBox() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int i = currentYear; i <= currentYear + 5; i++) {
            years.add(i);
        }
        yearComboBox.setItems(years);
        yearComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void updateCharts() {
        Integer selectedYear = yearComboBox.getValue();
        if (selectedYear != null) {
            updateDepensesChart(selectedYear);
            updateRevenusChart(selectedYear);
        }
    }

    private void updateDepensesChart(int year) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT NomBanque, MONTHNAME(DateDepense) AS Mois, SUM(Montant) AS TotalDepenses FROM depenses JOIN banques ON depenses.IDBanque = banques.IDBanque WHERE YEAR(DateDepense) = ? GROUP BY NomBanque, Mois");
            statement.setInt(1, year);
            ResultSet resultSet = statement.executeQuery();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                String mois = resultSet.getString("Mois");
                double totalDepenses = resultSet.getDouble("TotalDepenses");
                series.getData().add(new XYChart.Data<>(nomBanque + " - " + mois, totalDepenses));
            }

            depensesBarChart.getData().clear();
            depensesBarChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRevenusChart(int year) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT NomBanque, MONTHNAME(DateRevenu) AS Mois, SUM(Montant) AS TotalRevenus FROM revenus JOIN banques ON revenus.IDBanque = banques.IDBanque WHERE YEAR(DateRevenu) = ? GROUP BY NomBanque, Mois");
            statement.setInt(1, year);
            ResultSet resultSet = statement.executeQuery();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                String mois = resultSet.getString("Mois");
                double totalRevenus = resultSet.getDouble("TotalRevenus");
                series.getData().add(new XYChart.Data<>(nomBanque + " - " + mois, totalRevenus));
            }

            revenusBarChart.getData().clear();
            revenusBarChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}