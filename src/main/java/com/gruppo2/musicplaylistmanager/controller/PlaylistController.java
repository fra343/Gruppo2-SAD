package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.Optional;


 //Controller per creazione ed eliminazione playlist .
 //Visualizza le playlist come blocchi.

public class PlaylistController {

    private PlaylistService playlistService;
    private FlowPane playlistFlowPane;

    //Playlist attualmente selezionata 
    private Playlist selectedPlaylist = null;

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    //Collega il FlowPane al controller e registra un listener sull'ObservableList:
    //ogni aggiunta/rimozione di playlist aggiorna automaticamente le card.

    public void setPlaylistFlowPane(FlowPane playlistFlowPane) {
        this.playlistFlowPane = playlistFlowPane;
        playlistService.getAllPlaylists().addListener((ListChangeListener<Playlist>) change -> {
            rebuildCards();
        });
    }

    //Creazione playlist: con finestra per inserire il nome, poi delega a PlaylistService.
    @FXML
    public void onCreatePlaylist() {
        TextField nameField = new TextField();
        nameField.setPromptText("Nome della playlist");

        VBox content = new VBox(8, nameField);
        content.setStyle("-fx-padding: 10;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova playlist");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(e -> nameField.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistService.createPlaylist(nameField.getText());
                // Il listener sull'ObservableList aggiorna le card automaticamente
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // Elimina playlist: rimuove la card selezionata.
    @FXML
    public void onDeletePlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }
        playlistService.deletePlaylist(selectedPlaylist);
        selectedPlaylist = null;
        // Il listener aggiorna le card automaticamente
    }

    //Ricostruisce tutte le card visive nel pannello delle playlist
    private void rebuildCards() {
        playlistFlowPane.getChildren().clear();
        selectedPlaylist = null;
        for (Playlist p : playlistService.getAllPlaylists()) {
            playlistFlowPane.getChildren().add(createCard(p));
        }
    }

    // Crea una card visiva per la playlist: blocco grigio arrotondato con il nome.
    private Node createCard(Playlist playlist) {
        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(98);

        VBox card = new VBox(nameLabel);
        card.setPrefWidth(110);
        card.setPrefHeight(90);
        card.setAlignment(Pos.BOTTOM_LEFT);
        card.setStyle(styleCard(false));

        // Click: seleziona questa card e deseleziona le altre
        card.setOnMouseClicked(e -> {
            resetAllCards();
            card.setStyle(styleCard(true));
            selectedPlaylist = playlist;
        });

        return card;
    }

    //Rimuove la selezione visiva da tutte le card.
    private void resetAllCards() {
        for (Node node : playlistFlowPane.getChildren()) {
            node.setStyle(styleCard(false));
        }
    }

    private String styleCard(boolean selected) {
        String base = "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8;";
        return selected
                ? base + " -fx-background-color: #C8C8C8; -fx-border-color: #888; -fx-border-radius: 8; -fx-border-width: 2;"
                : base + " -fx-background-color: #E8E8E8;";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
