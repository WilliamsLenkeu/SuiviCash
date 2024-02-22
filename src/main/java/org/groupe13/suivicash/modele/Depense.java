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
    public String getNomBanque() {
        return nomBanque;
    }

    public void setNomBanque(String nomBanque) {
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

    public int getIdCategorie(){return idCategorie;}

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
            String sql = "SELECT `IDDepense`, `Montant`, `DateDepense` , `Description`,`depenses`.`IDBanque`, `IDCategorie`,`NomBanque` FROM `depenses` JOIN banques ON `depenses`.IDBanque = banques.IDBanque WHERE `IDCategorie` = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idCategorie);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Depense depense = new Depense();
                    depense.setIdDepense(resultSet.getInt("IDDepense"));
                    depense.setMontant(resultSet.getDouble("Montant"));
                    depense.setDateDepense(resultSet.getDate("DateDepense"));
                    depense.setNomBanque(resultSet.getString("NomBanque"));
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

    public void deleteDepense() {
        try {
            Connection connection = connectionFile.getConnection();
            String sql = "DELETE FROM depenses WHERE IDDepense = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, this.idDepense);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Dépense supprimée avec succès de la base de données.");
                } else {
                    System.out.println("Échec de la suppression de la dépense de la base de données.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la dépense de la base de données : " + e.getMessage());
        }
    }

    // Méthode pour récupérer toutes les dépenses depuis la base de données
    public List<Depense> getAllDepenses() {
        List<Depense> depenses = new ArrayList<>();

        connectionFile ConnectionFile = null;
        try (Connection connection = ConnectionFile.getConnection()) {
            String sql = "SELECT `IDDepense`, `Montant`, `DateDepense` , `Description`,`depenses`.`IDBanque`, `IDCategorie`,`NomBanque` FROM `depenses` JOIN banques ON `depenses`.IDBanque = banques.IDBanque";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int idDepense = resultSet.getInt("IDDepense");
                    double montant = resultSet.getDouble("Montant");
                    Date dateDepense = resultSet.getDate("DateDepense");
                    String description = resultSet.getString("Description");
                    int idBanque = resultSet.getInt("IDBanque");
                    int idCategorie = resultSet.getInt("IDCategorie");
                    String nomBanque= resultSet.getString("NomBanque");
                    Depense depense = new Depense(idDepense, montant, dateDepense, description, idBanque, idCategorie,nomBanque);
                    depenses.add(depense);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return depenses;
    }

}
