package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.groupe13.suivicash.modele.connectionFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AjoutDepenseController {
    public TextField descriptionField;
    public TextField montantField;
    public DatePicker datePicker;
    public ChoiceBox banqueChoiceBox;
    private Connection connection;
    private  String NomCategorie;
    public AjoutDepenseController(){}

    public AjoutDepenseController(String nom){
        this.NomCategorie= nom;
    }

    // Méthode appelée lors de l'initialisation du contrôleur
    @FXML
    private void initialize() {
        connection = connectionFile.getConnection();
        if (connection == null) {
            afficherErreur("Erreur de connexion", "Impossible de se connecter à la base de données.");
        } else {
            // Récupérer les noms des banques depuis la base de données et les ajouter au ChoiceBox
            List<String> nomsBanques = getNomsBanquesFromDB();
            if (nomsBanques.isEmpty()) {
                afficherErreur("Aucune banque trouvée", "Aucune banque n'est disponible dans la base de données.");
            } else {
                banqueChoiceBox.getItems().addAll(nomsBanques);
            }
        }
    }

    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Méthode pour récupérer l'ID de la banque correspondant au nom sélectionné
    private int getIdBanque(String nomBanque) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT IDBanque FROM banques WHERE NomBanque = ?");
        preparedStatement.setString(1, nomBanque);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("IDBanque");
        } else {
            return -1; // Indiquer qu'aucune banque n'a été trouvée
        }
    }

    static int getIdCategorieByNom(String categorieNom, Connection connection) throws SQLException {
        int idCategorie = -1;

        String sql = "SELECT `IDCategorie` FROM `categories` WHERE `NomCategorie` = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, MaSuperGlobale.NomCategorie);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idCategorie = resultSet.getInt("IDCategorie");
            }
        }

        return idCategorie;
    }

    public void HandleAjouterDepense(ActionEvent actionEvent) {
        // Récupérer les valeurs des champs
        LocalDate date = datePicker.getValue();
        String montant = montantField.getText();
        String description = descriptionField.getText(); // La description peut être vide

        // Vérifier si tous les champs obligatoires sont renseignés
        if (date != null && !montant.isEmpty()) {
            try {
                // Récupérer l'ID de la banque sélectionnée
                String nomBanque = banqueChoiceBox.getSelectionModel().getSelectedItem().toString();
                int idBanque = getIdBanque(nomBanque);

                // Récupérer l'ID de la catégorie
                int idCategorie = getIdCategorieByNom(NomCategorie, connection);

                // Insertion dans la base de données
                String sql = "INSERT INTO depenses (Montant, DateDepense, Description, IDBanque, IDCategorie) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, montant);
                    statement.setObject(2, date);
                    statement.setString(3, description);
                    statement.setInt(4, idBanque);
                    statement.setInt(5, idCategorie);

                    // Exécuter la requête
                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Dépense ajoutée avec succès.");
                    } else {
                        System.out.println("Échec de l'ajout de la dépense.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'ajout de la dépense dans la base de données : " + e.getMessage());
            }
        } else {
            // Afficher un message d'erreur indiquant que tous les champs obligatoires doivent être remplis
            System.out.println("Veuillez remplir tous les champs obligatoires.");
        }
    }

    private List<String> getNomsBanquesFromDB() {
        List<String> nomsBanques = new ArrayList<>();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT NomBanque FROM banques");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                nomsBanques.add(nomBanque);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des banques depuis la base de données : " + e.getMessage());
        }
        return nomsBanques;
    }

}
