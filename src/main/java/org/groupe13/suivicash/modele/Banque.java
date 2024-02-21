package org.groupe13.suivicash.modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Banque {
    private int IDBanque;
    private String nomBanque;
    private double solde;

    public Banque(int IDBanque, String nomBanque, double solde) {
        this.IDBanque = IDBanque;
        this.nomBanque = nomBanque;
        this.solde = solde;
    }

    public int getIDBanque() {
        return IDBanque;
    }

    public String getNomBanque() {
        return nomBanque;
    }

    public double getSolde() {
        return solde;
    }

    @Override
    public String toString() {
        return nomBanque;
    }

    // Méthode pour récupérer la liste des banques depuis la base de données
    public static List<Banque> getAllBanques(Connection connection) {
        List<Banque> banques = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM banques");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int IDBanque = resultSet.getInt("IDBanque");
                String nomBanque = resultSet.getString("NomBanque");
                double solde = resultSet.getDouble("Solde");
                Banque banque = new Banque(IDBanque, nomBanque, solde);
                banques.add(banque);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banques;
    }

    // Méthode pour récupérer le solde total de toutes les banques depuis la base de données
    public static double getTotalSolde(Connection connection) {
        double totalSolde = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(Solde) AS TotalSolde FROM banques");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalSolde = resultSet.getDouble("TotalSolde");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalSolde;
    }

    // Méthode pour récupérer les dépenses pour une banque donnée pour une année spécifique
    public static double getDepensesForYear(Connection connection, int IDBanque, int year) {
        double totalDepenses = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(Montant) AS TotalDepenses FROM depenses WHERE IDBanque = ? AND YEAR(DateDepense) = ?");
            statement.setInt(1, IDBanque);
            statement.setInt(2, year);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalDepenses = resultSet.getDouble("TotalDepenses");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalDepenses;
    }

    // Méthode pour récupérer les revenus pour une banque donnée pour une année spécifique
    public static double getRevenusForYear(Connection connection, int IDBanque, int year) {
        double totalRevenus = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(Montant) AS TotalRevenus FROM revenus WHERE IDBanque = ? AND YEAR(DateRevenu) = ?");
            statement.setInt(1, IDBanque);
            statement.setInt(2, year);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalRevenus = resultSet.getDouble("TotalRevenus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRevenus;
    }
}