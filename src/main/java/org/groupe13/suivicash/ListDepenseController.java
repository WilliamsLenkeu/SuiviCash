package org.groupe13.suivicash;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class ListDepenseController {

    @FXML
    private ListView<String> depensesListView;

    // Méthode appelée lorsqu'on clique sur une catégorie dans DepenseController
    public void afficherListeDepenses(String categorie) {
        // Obtenez la liste des dépenses pour la catégorie spécifiée depuis votre modèle de données
        List<String> listeDepenses = obtenirListeDepensesParCategorie(categorie);

        // Affichez la liste des dépenses dans la ListView
        depensesListView.getItems().setAll(listeDepenses);
    }

    // Méthode fictive pour obtenir la liste des dépenses par catégorie
    private List<String> obtenirListeDepensesParCategorie(String categorie) {
        // Implémentez votre logique pour récupérer les dépenses de la catégorie spécifiée depuis la base de données
        // ou tout autre modèle de données que vous utilisez dans votre application.
        // Cette méthode doit renvoyer la liste des noms des dépenses pour la catégorie donnée.
        // Vous devrez adapter cette méthode à votre modèle de données réel.
        // Pour cet exemple, je renvoie une liste fictive.
        return List.of("Dépense 1", "Dépense 2", "Dépense 3");
    }

    // Méthode pour gérer les actions sur les éléments de la liste des dépenses (si nécessaire)
    @FXML
    private void handleDepenseSelection(ActionEvent event) {
        // Implémentez le code pour gérer la sélection d'une dépense dans la liste (si nécessaire)
        // Vous pouvez utiliser event.getSource() pour obtenir l'élément sélectionné, par exemple.
        // Assurez-vous de définir cette méthode dans votre fichier FXML, si nécessaire.
    }
}
