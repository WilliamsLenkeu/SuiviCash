module org.groupe13.suivicash {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    opens org.groupe13.suivicash to javafx.fxml;
    exports org.groupe13.suivicash;
    exports org.groupe13.suivicash.modele;
    opens org.groupe13.suivicash.modele to javafx.fxml;
}