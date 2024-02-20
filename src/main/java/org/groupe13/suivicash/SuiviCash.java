package org.groupe13.suivicash;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SuiviCash extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SuiviCash.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 610);
        stage.setTitle("SuiviCash💰");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}