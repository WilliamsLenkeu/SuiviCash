package org.groupe13.suivicash.modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class Banque {
    private String nomBanque;
    private double solde;

    public Banque(String nomBanque, double solde) {
        this.nomBanque = nomBanque;
        this.solde = solde;
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
}
