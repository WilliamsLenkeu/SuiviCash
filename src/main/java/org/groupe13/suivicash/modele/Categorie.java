package org.groupe13.suivicash.modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Categorie {

    private int id;
    private String nomCategorie;
    private double totalDepense;

    // Constructeur, getters et setters

    // Constructeurs
    public Categorie() {
    }

    public Categorie(int id, String nomCategorie) {
        this.id = id;
        this.nomCategorie = nomCategorie;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public double getTotalDepense() {
        return totalDepense;
    }

    public void setTotalDepense(double totalDepense) {
        this.totalDepense = totalDepense;
    }

    // Méthode pour calculer le total des dépenses de la catégorie
    public void calculerTotalDepense(List<Depense> depenses) {
        double total = 0.0;

        for (Depense depense : depenses) {
            if (depense.getIdCategorie() == this.id) {
                total += depense.getMontant();
            }
        }

        this.totalDepense = total;
    }

    // Méthode pour récupérer toutes les catégories depuis la base de données
    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();

        connectionFile ConnectionFile = null;
        try (Connection connection = ConnectionFile.getConnection()) {
            String sql = "SELECT `IDCategorie`, `NomCategorie` FROM `categories`";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int idCategorie = resultSet.getInt("IDCategorie");
                    String nomCategorie = resultSet.getString("NomCategorie");
                    Depense dep = new Depense();
                    List<Depense> MesDept= dep.getAllDepenses();

                    Categorie categorie = new Categorie(idCategorie, nomCategorie);
                    categorie.calculerTotalDepense(MesDept);
                    categories.add(categorie);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
