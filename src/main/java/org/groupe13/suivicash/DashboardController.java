package org.groupe13.suivicash;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import org.groupe13.suivicash.modele.Banque;
import org.groupe13.suivicash.modele.Revenus;
import org.groupe13.suivicash.modele.connectionFile;

import java.sql.Connection;
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

    @FXML
    private TableColumn<Banque, Button> deleteCol;

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

            deleteCol.setCellValueFactory(param -> {
                Button deleteButton = new Button("Supprimer");
                deleteButton.setOnAction(event -> {
                    handleDeleteBanque(param.getValue());
                });
                return new SimpleObjectProperty<>(deleteButton);
            });
        } else {
            // Gérer le cas où la connexion à la base de données a échoué
            System.err.println("La connexion à la base de données a échoué.");
        }
    }

    @FXML
    private void handleDeleteBanque(Banque banque) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression de la banque : " + banque.getNomBanque());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette banque ?\nCette action est irréversible et supprimera" +
                "aussi les depenses et revenus de cette banque");

        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeYes) {
                // Suppression de la banque
                try {
                    Banque.deleteBanque(connection, banque.getIDBanque());
                    loadBanquesFromDatabase();
                    calculerTotalSolde();
                    updateCharts();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Une erreur est survenue lors de la suppression de la banque.");
                    errorAlert.setContentText("Veuillez réessayer plus tard.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    @FXML
    private void handleAjouterBanqueClick() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vues/AjoutBanque.fxml"));
            Parent root = loader.load();
            stage.setTitle("Ajouter Banque");
            stage.setScene(new Scene(root));
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
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Banque banque : banques) {
            double totalDepenses = Banque.getDepensesForYear(connection, banque.getIDBanque(), year);
            series.getData().add(new XYChart.Data<>(banque.getNomBanque(), totalDepenses));
        }
        depensesBarChart.getData().clear();
        depensesBarChart.getData().add(series);
    }

    private void updateRevenusChart(int year) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Banque banque : banques) {
            double totalRevenus = Banque.getRevenusForYear(connection, banque.getIDBanque(), year);
            series.getData().add(new XYChart.Data<>(banque.getNomBanque(), totalRevenus));
        }
        revenusBarChart.getData().clear();
        revenusBarChart.getData().add(series);
    }
}