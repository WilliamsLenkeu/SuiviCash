module org.groupe13.suivicash {
    requires javafx.controls;
    requires javafx.fxml;


    requires java.sql;
    requires java.mail;

    opens org.groupe13.suivicash to javafx.fxml;
    exports org.groupe13.suivicash;
    exports org.groupe13.suivicash.modele;
    opens org.groupe13.suivicash.modele to javafx.fxml;
}