package org.groupe13.suivicash.modele;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.Date;

public class Revenus {
    private int IDRevenu;
    private double montant;
    private Date DateRevenu;
    private String description;
    private String nomBanque;
    private String typeRepetition;
    private Integer periodeRepetition;

    // Constructeurs
    public Revenus() {
    }

    public Revenus(int IDRevenu, double montant, Date DateRevenu, String description, String nomBanque, String typeRepetition, Integer periodeRepetition) {
        this.IDRevenu = IDRevenu;
        this.montant = montant;
        this.DateRevenu = DateRevenu;
        this.description = description;
        this.nomBanque = nomBanque;
        this.typeRepetition = typeRepetition;
        this.periodeRepetition = periodeRepetition;
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

    public String getNomBanque() {
        return nomBanque;
    }

    public void setNomBanque(String nomBanque) {
        this.nomBanque = nomBanque;
    }

    public String getTypeRepetition() {
        return typeRepetition;
    }

    public void setTypeRepetition(String typeRepetition) {
        this.typeRepetition = typeRepetition;
    }

    public Integer getPeriodeRepetition() {
        return periodeRepetition;
    }

    public void setPeriodeRepetition(Integer periodeRepetition) {
        this.periodeRepetition = periodeRepetition;
    }

    public List<Revenus> getRevenus() {
        List<Revenus> revenusList = new ArrayList<>();

        try (Connection connection = connectionFile.getConnection()) {
            String sql = "SELECT revenus.IDRevenu, revenus.Montant, revenus.DateRevenu, revenus.Description, " +
                    "revenus.TypeRepetition, revenus.PeriodeRepetition, banques.NomBanque FROM revenus JOIN banques ON revenus.IDBanque = banques.IDBanque";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Revenus revenus = new Revenus();
                    revenus.setIDRevenu(resultSet.getInt("IDRevenu"));
                    revenus.setMontant(resultSet.getDouble("Montant"));
                    revenus.setDateRevenu(resultSet.getDate("DateRevenu"));
                    revenus.setDescription(resultSet.getString("Description"));
                    revenus.setTypeRepetition(resultSet.getString("TypeRepetition"));
                    revenus.setPeriodeRepetition(resultSet.getInt("PeriodeRepetition"));
                    revenus.setNomBanque(resultSet.getString("NomBanque"));

                    revenusList.add(revenus);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée dans votre application
        }

        return revenusList;
    }

    // Méthode pour récupérer les noms des banques depuis la base de données
    public List<String> getNomsBanques() {
        List<String> nomsBanques = new ArrayList<>();
        try (Connection connection = connectionFile.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT NomBanque FROM banques");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String nomBanque = resultSet.getString("NomBanque");
                nomsBanques.add(nomBanque);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des banques depuis la base de données : " + e.getMessage());
        }
        return nomsBanques;
    }

    // Méthode pour ajouter un revenu à la base de données
    public void ajouterRevenu(double montant, Date date, String description, String nomBanque, String typeRepetition, Integer periodeRepetition) {
        try (Connection connection = connectionFile.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO revenus (Montant, DateRevenu, Description, IDBanque, TypeRepetition, PeriodeRepetition) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setDouble(1, montant);
            preparedStatement.setDate(2, date);
            preparedStatement.setString(3, description);
            int idBanque = getIdBanque(nomBanque);
            preparedStatement.setInt(4, idBanque);
            preparedStatement.setString(5, typeRepetition);
            preparedStatement.setInt(6, periodeRepetition);
            preparedStatement.executeUpdate();

            // Mettre à jour le solde de la banque correspondante
            crediterSoldeBanque(idBanque, montant);

            afficherInformation("Succès", "Le revenu a été ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion du revenu dans la base de données : " + e.getMessage());
            afficherErreur("Erreur", "Une erreur s'est produite lors de l'ajout du revenu.");
        }
    }

    // Méthode pour créditer le solde de la banque correspondante
    public void crediterSoldeBanque(int idBanque, double montant) {
        try (Connection connection = connectionFile.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE banques SET Solde = Solde + ? WHERE IDBanque = ?");
            statement.setDouble(1, montant);
            statement.setInt(2, idBanque);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Solde de la banque mis à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour du solde de la banque.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du solde de la banque : " + e.getMessage());
        }
    }

    public void deleteRevenus() {
        try {
            Connection connection = connectionFile.getConnection();
            String sqlHistorique = "DELETE FROM historique_revenus WHERE IDRevenu = ?";
            String sqlRevenus = "DELETE FROM revenus WHERE IDRevenu = ?";

            // Supprimer d'abord les entrées dans l'historique des revenus
            try (PreparedStatement statementHistorique = connection.prepareStatement(sqlHistorique)) {
                statementHistorique.setInt(1, this.IDRevenu);
                statementHistorique.executeUpdate();
            }

            // Ensuite, supprimer le revenu lui-même
            try (PreparedStatement statementRevenus = connection.prepareStatement(sqlRevenus)) {
                statementRevenus.setInt(1, this.IDRevenu);
                int rowsAffected = statementRevenus.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Revenus supprimée avec succès de la base de données.");
                } else {
                    System.out.println("Échec de la suppression du revenus de la base de données.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du revenus de la base de données : " + e.getMessage());
        }
    }

    // Méthode pour récupérer l'ID de la banque correspondant au nom sélectionné
    public int getIdBanque(String nomBanque) throws SQLException {
        try (Connection connection = connectionFile.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT IDBanque FROM banques WHERE NomBanque = ?");
            preparedStatement.setString(1, nomBanque);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("IDBanque");
            } else {
                return -1; // Indiquer qu'aucune banque n'a été trouvée
            }
        }
    }

    // Méthode pour afficher une alerte d'erreur
    private void afficherErreur(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Méthode pour effectuer la répétition du revenu en incrémentant le solde de la banque correspondante
    public void effectuerRepetitionRevenu() throws SQLException {
        // Récupérer la liste des revenus
        List<Revenus> revenusList = getRevenus();

        // Récupérer la date actuelle
        Calendar cal = Calendar.getInstance();
        Date currentDate = new Date(cal.getTimeInMillis());

        // Créer une instance de HistoriqueRevenus
        HistoriqueRevenus historiqueRevenus = new HistoriqueRevenus();

        // Parcourir la liste des revenus
        for (Revenus revenus : revenusList) {
            Date dateRevenu = revenus.getDateRevenu();
            String typeRepetition = revenus.getTypeRepetition();
            Integer periodeRepetition = revenus.getPeriodeRepetition();

            // Calculer la différence de temps entre la date actuelle et la date du revenu
            long differenceMillis = currentDate.getTime() - dateRevenu.getTime();
            long differenceDays = differenceMillis / (24 * 60 * 60 * 1000); // Convertir en jours

            // Vérifier si la période de répétition est écoulée par rapport à la date du revenu
            boolean repetitionDue = false;
            switch (typeRepetition) {
                case "jour":
                    repetitionDue = differenceDays >= periodeRepetition;
                    break;
                case "semaine":
                    repetitionDue = differenceDays >= periodeRepetition * 7;
                    break;
                case "mois":
                    repetitionDue = differenceDays >= periodeRepetition * 30; // Approximation de 30 jours par mois
                    break;
                case "année":
                    repetitionDue = differenceDays >= periodeRepetition * 365; // Approximation de 365 jours par année
                    break;
            }

            // Si la répétition est due, incrémenter le solde de la banque correspondante et enregistrer l'historique
            if (repetitionDue) {
                int idBanque = getIdBanque(revenus.getNomBanque());
                double montant = revenus.getMontant();

                // Vérifier si la répétition pour cette date existe déjà dans l'historique
                boolean repetitionExistante = historiqueRevenus.checkRepetitionExistante(revenus.getIDRevenu(), currentDate);

                // Si aucune répétition n'existe, l'ajouter dans l'historique et incrémenter le solde de la banque correspondante
                if (!repetitionExistante) {
                    historiqueRevenus.ajouterHistoriqueRevenu(revenus.getIDRevenu(), currentDate);
                    crediterSoldeBanque(idBanque, montant);
                }
            }
        }
    }

    // Méthode pour afficher une alerte d'information
    private void afficherInformation(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}