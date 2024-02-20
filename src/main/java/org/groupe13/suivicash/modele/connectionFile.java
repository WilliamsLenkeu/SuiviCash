package org.groupe13.suivicash.modele;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public static void ajouterCategorie(String nomCategorie, int type) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Établir la connexion à la base de données
            connection = getConnection();

            // Requête SQL pour l'insertion d'une nouvelle catégorie
            String sql = "INSERT INTO categories (NomCategorie,Type) VALUES (?,?)";

            // Préparer la déclaration
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nomCategorie);
            preparedStatement.setInt(2, type);



            // Exécuter la mise à jour de la base de données
            preparedStatement.executeUpdate();

            System.out.println("Catégorie ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        } finally {
            // Fermer la connexion et la déclaration
            closeConnection(connection);
            closeStatement(preparedStatement);
        }
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
}