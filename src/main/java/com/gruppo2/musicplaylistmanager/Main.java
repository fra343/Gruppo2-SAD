package com.gruppo2.musicplaylistmanager;

import com.gruppo2.musicplaylistmanager.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gruppo2/musicplaylistmanager/view/Main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setTitle("Music Playlist Manager");
        primaryStage.setScene(new Scene(root, screenBounds.getWidth(), screenBounds.getHeight()));
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setResizable(false);
        // US-18a: salva libreria e playlist al momento della chiusura dell'applicazione.
        primaryStage.setOnCloseRequest(e -> controller.saveAll());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}