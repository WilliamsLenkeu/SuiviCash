package org.groupe13.suivicash.modele;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoriqueRevenus {

    // Méthode pour vérifier si une répétition existe déjà pour un revenu donné à une date donnée
    public boolean checkRepetitionExistante(int idRevenu, Date date) {
        boolean repetitionExistante = false;
        try (Connection connection = connectionFile.getConnection()) {
            String sql = "SELECT * FROM historique_revenus WHERE IDRevenu = ? AND DateRepetition = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idRevenu);
                statement.setDate(2, new java.sql.Date(date.getTime()));
                ResultSet resultSet = statement.executeQuery();
                repetitionExistante = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repetitionExistante;
    }

    // Méthode pour ajouter une entrée dans l'historique des revenus
    public void ajouterHistoriqueRevenu(int idRevenu, Date date) {
        try (Connection connection = connectionFile.getConnection()) {
            String sql = "INSERT INTO historique_revenus (IDRevenu, DateRepetition) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idRevenu);
                statement.setDate(2, new java.sql.Date(date.getTime()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}