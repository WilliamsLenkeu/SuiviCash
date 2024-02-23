package org.groupe13.suivicash.modele;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.groupe13.suivicash.modele.MotDePasse.ConnectionFile;

public class HistoriqueBanques {
    private int idHistorique;
    private int idBanque;
    private double ancienSolde;
    private double nouveauSolde;
    private String dateMiseAJour;

    // Constructeur, getters et setters

    // Constructeurs
    public HistoriqueBanques() {
    }

    public HistoriqueBanques(int idHistorique, int idBanque, double ancienSolde, double nouveauSolde, String dateMiseAJour) {
        this.idHistorique = idHistorique;
        this.idBanque = idBanque;
        this.ancienSolde = ancienSolde;
        this.nouveauSolde = nouveauSolde;
        this.dateMiseAJour = dateMiseAJour;
    }

    // Getters et setters
    public int getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(int idHistorique) {
        this.idHistorique = idHistorique;
    }

    public int getIdBanque() {
        return idBanque;
    }

    public void setIdBanque(int idBanque) {
        this.idBanque = idBanque;
    }

    public double getAncienSolde() {
        return ancienSolde;
    }

    public void setAncienSolde(double ancienSolde) {
        this.ancienSolde = ancienSolde;
    }

    public double getNouveauSolde() {
        return nouveauSolde;
    }

    public void setNouveauSolde(double nouveauSolde) {
        this.nouveauSolde = nouveauSolde;
    }

    public String getDateMiseAJour() {
        return dateMiseAJour;
    }

    public void setDateMiseAJour(String dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }

    // Méthode pour récupérer l'historique de la banque par son ID
    public List<HistoriqueBanques> getHistoriqueBanqueById(int idBanque) {
        List<HistoriqueBanques> historiqueBanquesList = new ArrayList<>();

        try {
            Connection connection = ConnectionFile.getConnection();

            String sql = "SELECT * FROM Historique_Banque WHERE IDBanque = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idBanque);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    HistoriqueBanques historiqueBanques = new HistoriqueBanques();
                    historiqueBanques.setIdHistorique(resultSet.getInt("IDHistorique"));
                    historiqueBanques.setIdBanque(resultSet.getInt("IDBanque"));
                    historiqueBanques.setAncienSolde(resultSet.getDouble("AncienSolde"));
                    historiqueBanques.setNouveauSolde(resultSet.getDouble("NouveauSolde"));

                    // Convertir le timestamp en une chaîne de date formatée
                    Date date = resultSet.getTimestamp("DateMiseAJour");
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMMM yyyy");
                    String formattedDate = sdf.format(date);
                    historiqueBanques.setDateMiseAJour(formattedDate);

                    historiqueBanquesList.add(historiqueBanques);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historiqueBanquesList;
    }
}

