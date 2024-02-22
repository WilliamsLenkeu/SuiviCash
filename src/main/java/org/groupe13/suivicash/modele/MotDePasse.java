package org.groupe13.suivicash.modele;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MotDePasse {
    private int idUtilisateur;
    private String motDePasse;

    // Constructeurs
    public MotDePasse() {
    }

    public MotDePasse(int idUtilisateur, String motDePasse) {
        this.idUtilisateur = idUtilisateur;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    static connectionFile ConnectionFile;
    // Méthode pour insérer un mot de passe dans la base de données
    public boolean insererMotDePasse() {
        try (Connection connection = ConnectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO motsdepasse (IDUtilisateur, MotDePasse) VALUES (?, ?)")) {

            preparedStatement.setInt(1, getIdUtilisateur());

            // Hasher le mot de passe avec BCrypt avant de l'insérer

            String motDePasseHash = BCrypt.hashpw(getMotDePasse(), BCrypt.gensalt());
            preparedStatement.setString(2, motDePasseHash);

            preparedStatement.executeUpdate();

            System.out.println("Mot de passe inséré avec succès.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
        // Méthode pour supprimer un mot de passe de la base de données
    public void supprimerMotDePasse() {
        try (Connection connection = ConnectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM motsdepasse WHERE IDUtilisateur = ?")) {

            preparedStatement.setInt(1, getIdUtilisateur());

            preparedStatement.executeUpdate();

            System.out.println("Mot de passe supprimé avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour modifier un mot de passe dans la base de données
    public void modifierMotDePasse() {
        try (Connection connection = ConnectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE motsdepasse SET MotDePasse = ? WHERE IDUtilisateur = ?")) {

            preparedStatement.setString(1, getMotDePasse());
            preparedStatement.setInt(2, getIdUtilisateur());

            preparedStatement.executeUpdate();

            System.out.println("Mot de passe modifié avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer tous les mots de passe de la base de données
    public static List<MotDePasse> recupererTousLesMotsDePasse() {
        List<MotDePasse> motsDePasseList = new ArrayList<>();

        try  {
            Connection connection = ConnectionFile.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM motsdepasse");
            while (resultSet.next()) {
                int idUtilisateur = resultSet.getInt("IDUtilisateur");
                String motDePasse = resultSet.getString("MotDePasse");

                MotDePasse motDePasseObj = new MotDePasse(idUtilisateur, motDePasse);
                motsDePasseList.add(motDePasseObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return motsDePasseList;
    }

}
