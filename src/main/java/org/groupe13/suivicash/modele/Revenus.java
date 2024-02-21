package org.groupe13.suivicash.modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Revenus {
    private int IDRevenu;
    private double montant;
    private Date DateRevenu;
    private String description;
    private int idBanque; // Modifier le nom de la propriété en minuscules

    // Constructeurs
    public Revenus() {
    }

    public Revenus(int IDRevenu, double montant, Date DateRevenu, String description, int idBanque) {
        this.IDRevenu = IDRevenu;
        this.montant = montant;
        this.DateRevenu = DateRevenu;
        this.description = description;
        this.idBanque = idBanque;
    }

    // Getters et setters
    public int getIDRevenu() {
        return IDRevenu;
    }

    public void setIDRevenu(int IDRevenu) {
        this.IDRevenu = IDRevenu;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Date getDateRevenu() {
        return DateRevenu;
    }

    public void setDateRevenu(Date DateRevenu) {
        this.DateRevenu = DateRevenu;
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

    // Méthode pour récupérer les revenus en fonction du montant
    public List<Revenus> getRevenus() {
        List<Revenus> revenusList = new ArrayList<>();

        try (Connection connection = connectionFile.getConnection()) {
            String sql = "SELECT `IDRevenu`,`Montant`, `DateRevenu`, `Description`, `IDBanque` FROM `revenus`";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Revenus revenus = new Revenus();
                    revenus.setIDRevenu(resultSet.getInt("IDRevenu"));
                    revenus.setMontant(resultSet.getInt("Montant"));
                    revenus.setDateRevenu(resultSet.getDate("DateRevenu"));
                    revenus.setDescription(resultSet.getString("Description"));
                    revenus.setIdBanque(resultSet.getInt("IDBanque"));

                    revenusList.add(revenus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée dans votre application
        }

        return revenusList;
    }
}