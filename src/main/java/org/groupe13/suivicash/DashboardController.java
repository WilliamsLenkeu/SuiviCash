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
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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

    private ObservableList<Banque> banques;

    private Connection connection;

    @FXML
    private void initialize() {
        connection = connectionFile.getConnection();
        loadBanquesFromDatabase();
        calculerTotalSolde();
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

    /*public void setStage(Stage stage) {
        this.stage = stage;
    }*/

    private void loadBanquesFromDatabase() {
        banques = FXCollections.observableArrayList();
        banques.addAll(Banque.getAllBanques(connection));
        banqueTableView.setItems(banques);
    }

    private void calculerTotalSolde() {
        double totalSolde = Banque.getTotalSolde(connection);
        totalSoldeLabel.setText(String.format("%.2f", totalSolde));
    }
}
