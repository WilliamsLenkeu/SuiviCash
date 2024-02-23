package org.groupe13.suivicash;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.groupe13.suivicash.modele.HistoriqueBanques;

import java.util.List;

public class HistoriqueController {

    @FXML
    private TableView<HistoriqueBanques> historiqueTable;

    @FXML
    private TableColumn<HistoriqueBanques, Integer> idHistoriqueColumn;

    @FXML
    private TableColumn<HistoriqueBanques, Double> ancienSoldeColumn;

    @FXML
    private TableColumn<HistoriqueBanques, Double> nouveauSoldeColumn;

    @FXML
    private TableColumn<HistoriqueBanques, String> dateMiseAJourColumn;

    private ObservableList<HistoriqueBanques> historiqueList = FXCollections.observableArrayList();

    // Méthode appelée lors de l'initialisation du contrôleur
    @FXML
    private void initialize() {
        // Associer les colonnes de la TableView avec les propriétés de l'objet HistoriqueBanques

        ancienSoldeColumn.setCellValueFactory(new PropertyValueFactory<>("ancienSolde"));
        nouveauSoldeColumn.setCellValueFactory(new PropertyValueFactory<>("nouveauSolde"));
        dateMiseAJourColumn.setCellValueFactory(new PropertyValueFactory<>("dateMiseAJour"));

        loadHistorique(MaSuperGlobale.IDBanque);

    }

    // Méthode pour charger l'historique de la banque
    private void loadHistorique(int idBanque) {
        HistoriqueBanques historiqueBanques = new HistoriqueBanques();
        List<HistoriqueBanques> historique = historiqueBanques.getHistoriqueBanqueById(idBanque);
        historiqueList.addAll(historique);

        // Ajouter la liste observable à la TableView
        historiqueTable.setItems(historiqueList);
    }
}
