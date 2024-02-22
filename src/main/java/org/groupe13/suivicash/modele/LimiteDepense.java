package org.groupe13.suivicash.modele;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LimiteDepense {

    private int id;
    private double limite;
    public connectionFile ConnectionFile;

    // Constructeurs, getters et setters

    public LimiteDepense() {
    }

    public LimiteDepense(int id, double limite) {
        this.id = id;
        this.limite = limite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    // Méthode pour ajouter une nouvelle limite de dépense
    public void ajouterLimiteDepense(double limiteMensuelle) {
        try (Connection connection = ConnectionFile.getConnection()) {
            String sql = "INSERT INTO limitesdepenses (LimiteMensuelle) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, limiteMensuelle);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Limite de dépense ajoutée avec succès.");
                } else {
                    System.out.println("Échec de l'ajout de la limite de dépense.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer une limite de dépense par ID
    public void supprimerLimiteDepense(int idLimite) {
        try (Connection connection = ConnectionFile.getConnection()) {
            String sql = "DELETE FROM limitesdepenses WHERE IDLimite = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idLimite);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Limite de dépense supprimée avec succès.");
                } else {
                    System.out.println("Échec de la suppression de la limite de dépense.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour une limite de dépense par ID
    public void mettreAJourLimiteDepense(int idLimite, double nouvelleLimite) {
        try (Connection connection = ConnectionFile.getConnection()) {
            String sql = "UPDATE limitesdepenses SET LimiteMensuelle = ? WHERE IDLimite = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, nouvelleLimite);
                statement.setInt(2, idLimite);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Limite de dépense mise à jour avec succès.");
                } else {
                    System.out.println("Échec de la mise à jour de la limite de dépense.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer toutes les limites de dépenses
    public List<LimiteDepense> getLimites() {
        List<LimiteDepense> limites = new ArrayList<>();

        try {
            Connection connection = ConnectionFile.getConnection();
            String sql = "SELECT `IDLimite`, `LimiteMensuelle` FROM `limitesdepenses`";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    LimiteDepense limite = new LimiteDepense();
                    limite.setId(resultSet.getInt("IDLimite"));
                    limite.setLimite(resultSet.getDouble("LimiteMensuelle"));
                    limites.add(limite);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return limites;
    }
}
