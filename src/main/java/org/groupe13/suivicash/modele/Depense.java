package org.groupe13.suivicash.modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Depense {
    public  connectionFile connectionFile;
    private int idDepense;
    private double montant;
    private Date dateDepense;

    private String description;
    private int idBanque;
    private int idCategorie;

    private String nomBanque;

    // Constructeur, getters et setters

    // Constructeurs
    public Depense() {
    }

    public Depense(int idDepense, double montant, Date dateDepense, String description, int idBanque, int idCategorie,String nomBanque ) {
        this.idDepense = idDepense;
        this.montant = montant;
        this.dateDepense = dateDepense;
        this.description = description;
        this.idBanque = idBanque;
        this.idCategorie = idCategorie;
        this.nomBanque = nomBanque;
    }

    // Getters et setters

    public String getnomBanque() {
        return nomBanque;
    }

    public void setnomBanque(String nomBanque) {
        this.nomBanque = nomBanque;
    }


    public int getIdDepense() {
        return idDepense;
    }

    public void setIdDepense(int idDepense) {
        this.idDepense = idDepense;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDateDepense() {
        return dateDepense;
    }

    public void setDateDepense(Date dateDepense) {
        this.dateDepense = dateDepense;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdBanque() {
        return idBanque;
    }

    public void setIdBanque(int idBanque) {
        this.idBanque = idBanque;
    }


    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    // Méthode pour récupérer les dépenses en fonction du nom de la catégorie
    public  List<Depense> getDepensesByCategorie(String categorieNom) {
        List<Depense> depenses = new ArrayList<>();

        try {
            Connection connection = org.groupe13.suivicash.modele.connectionFile.getConnection();

            // Récupérez l'ID de la catégorie en fonction du nom
            int idCategorie = getIdCategorieByNom(categorieNom, connection);

            // Effectuez une requête SQL pour récupérer les dépenses en fonction de l'ID de la catégorie
            String sql = "SELECT `IDDepense`, `Montant`, `DateDepense` , `Description`, `IDBanque`, `IDCategorie` FROM `depenses` WHERE `IDCategorie` = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idCategorie);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Depense depense = new Depense();
                    depense.setIdDepense(resultSet.getInt("IDDepense"));
                    depense.setMontant(resultSet.getDouble("Montant"));
                    depense.setDateDepense(resultSet.getDate("DateDepense"));
                    depense.setnomBanque(getBankNameById(resultSet.getInt("IDBanque")));
                    depense.setDescription(resultSet.getString("Description"));
                    depense.setIdBanque(resultSet.getInt("IDBanque"));
                    depense.setIdCategorie(resultSet.getInt("IDCategorie"));
                    depenses.add(depense);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée dans votre application
        }

        return depenses;
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

    // Méthode utilitaire pour obtenir l'ID de la catégorie en fonction du nom
    private static int getIdCategorieByNom(String categorieNom, Connection connection) throws SQLException {
        int idCategorie = -1;

        String sql = "SELECT `IDCategorie` FROM `categories` WHERE `NomCategorie` = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categorieNom);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idCategorie = resultSet.getInt("IDCategorie");
            }
        }

        return idCategorie;
    }

}
