package org.groupe13.suivicash.modele;

import javafx.scene.control.Alert;
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
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO motsdepasse ( MotDePasse) VALUES ( ?)")) {


            // Hasher le mot de passe avec BCrypt avant de l'insérer
            String motDePasseHash = BCrypt.hashpw(getMotDePasse(), BCrypt.gensalt());
            preparedStatement.setString(1, motDePasseHash);

            preparedStatement.executeUpdate();

            afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe inséré avec succès.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour supprimer un mot de passe de la base de données
    public void supprimerMotDePasse() {
        try (Connection connection = ConnectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM motsdepasse WHERE IDUtilisateur = ?")) {

            preparedStatement.setInt(1, getIdUtilisateur());

            preparedStatement.executeUpdate();

            afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe supprimé avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour modifier un mot de passe dans la base de données
    public void modifierMotDePasse() {
        try (Connection connection = ConnectionFile.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE motsdepasse SET MotDePasse = ? WHERE IDUtilisateur = ?")) {

            // Hasher le mot de passe avec BCrypt avant de l'insérer
            String motDePasseHash = BCrypt.hashpw(getMotDePasse(), BCrypt.gensalt());
            preparedStatement.setString(1, motDePasseHash);
            preparedStatement.setInt(2, getIdUtilisateur());

            preparedStatement.executeUpdate();

            afficherBoiteDialogue(Alert.AlertType.INFORMATION, "Succès", "Mot de passe modifié avec succès.");
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

    // Méthode pour afficher une boîte de dialogue
    private void afficherBoiteDialogue(Alert.AlertType type, String titre, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
