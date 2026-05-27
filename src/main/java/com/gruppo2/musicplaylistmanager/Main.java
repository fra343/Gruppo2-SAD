package com.gruppo2.musicplaylistmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// Prova iniziale per verificare che il setup funzioni.
// Da sostituire con la versione che carica Main.fxml.
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hello, Music Playlist Manager!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Music Playlist Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
