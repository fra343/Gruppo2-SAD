package com.gruppo2.musicplaylistmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gruppo2/musicplaylistmanager/view/Main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Music Playlist Manager");
        primaryStage.setScene(new Scene(root, 1000, 680));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}