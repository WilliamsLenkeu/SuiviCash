package org.groupe13.suivicash;
import javafx.scene.control.Button;
import  org.groupe13.suivicash.modele.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class ListDepenseController {

    public Button AjouterDepenseButton;
    private String categorieSelectionnee;
    private List<Depense> MesDep;

    
    private void initialize(){

    }
    public  ListDepenseController(){

    }
    public ListDepenseController(List<Depense> Maliste){
        MesDep = new ArrayList<Depense>();
        MesDep= Maliste;
    }

    public void handleAjouterDepenseClick(ActionEvent actionEvent) {
    }
}
