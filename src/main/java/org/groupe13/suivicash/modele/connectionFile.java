package org.groupe13.suivicash.modele;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class connectionFile {
    // Les informations de connexion à la base de données MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/suivicash";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Méthode pour établir la connexion à la base de données
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion réussie à la base de données MySQL !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données MySQL : " + e.getMessage());
        }
        return connection;
    }

    // Méthode pour fermer la connexion à la base de données
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base de données fermée avec succès !");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion à la base de données : " + e.getMessage());
            }
        }
    }

    // Méthode pour ajouter une catégorie à la base de données
    public static boolean ajouterCategorie(String nomCategorie, int type) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean ajoutReussi = false;

        try {
            // Établir la connexion à la base de données
            connection = getConnection();

            // Vérifier si la catégorie existe déjà
            String verificationSql = "SELECT COUNT(*) FROM categories WHERE NomCategorie = ?";
            preparedStatement = connection.prepareStatement(verificationSql);
            preparedStatement.setString(1, nomCategorie);
            resultSet = preparedStatement.executeQuery();

            // Si la catégorie existe déjà, afficher un message d'erreur
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                afficherBoiteDialogueInformation("La catégorie '" + nomCategorie + "' existe déjà. Veuillez choisir un nom différent.");
            } else {
                // Requête SQL pour l'insertion d'une nouvelle catégorie
                String ajoutSql = "INSERT INTO categories (NomCategorie, Type) VALUES (?, ?)";

                // Préparer la déclaration
                preparedStatement = connection.prepareStatement(ajoutSql);
                preparedStatement.setString(1, nomCategorie);
                preparedStatement.setInt(2, type);

                // Exécuter la mise à jour de la base de données
                int rowsAffected = preparedStatement.executeUpdate();

                // Si une ligne a été affectée, l'ajout est réussi
                if (rowsAffected > 0) {
                    afficherBoiteDialogueInformation("Catégorie ajoutée avec succès !");
                    ajoutReussi = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        } finally {
            // Fermer la connexion, la déclaration et le résultat
            closeConnection(connection);
            closeStatement(preparedStatement);
            closeResultSet(resultSet);
        }

        return ajoutReussi;
    }

    private static void afficherBoiteDialogueInformation(String s) {
    }

    // Méthode pour récupérer toutes les catégories de la base de données
    public static List<String> recupererNomCategories() {
        List<String> categories = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Établir la connexion à la base de données
            connection = getConnection();

            // Requête SQL pour récupérer toutes les catégories
            String sql = "SELECT NomCategorie FROM categories";

            // Préparer la déclaration
            preparedStatement = connection.prepareStatement(sql);

            // Exécuter la requête
            resultSet = preparedStatement.executeQuery();

            // Parcourir les résultats et ajouter les catégories à la liste
            while (resultSet.next()) {
                String nomCategorie = resultSet.getString("NomCategorie");
                categories.add(nomCategorie);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        } finally {
            // Fermer la connexion, la déclaration et le résultat
            closeConnection(connection);
            closeStatement(preparedStatement);
            closeResultSet(resultSet);
        }

        return categories;
    }

    // Méthode pour fermer la déclaration
    public static void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
                System.out.println("Déclaration fermée avec succès !");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la déclaration : " + e.getMessage());
            }
        }
    }

    // Méthode pour fermer le résultat
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
                System.out.println("Résultat fermé avec succès !");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture du résultat : " + e.getMessage());
            }
        }
    }
}