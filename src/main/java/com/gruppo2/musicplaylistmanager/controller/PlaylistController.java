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

/**
 * Controller per la creazione ed eliminazione delle playlist.
 * Visualizza le playlist come card cliccabili nel pannello destro e mantiene
 * il riferimento alla playlist attualmente selezionata.
 */
public class PlaylistController {

    private PlaylistService playlistService;
    private FlowPane playlistFlowPane;

    /** Playlist attualmente selezionata tramite click su una card, o {@code null}. */
    private Playlist selectedPlaylist = null;

    /**
     * Inietta il servizio delle playlist.
     *
     * @param playlistService servizio condiviso con gli altri controller
     */
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * Collega il {@link FlowPane} al controller e registra un listener
     * sull'ObservableList delle playlist: ogni aggiunta o rimozione di playlist
     * ricostruisce automaticamente le card nel pannello.
     *
     * @param playlistFlowPane pannello in cui vengono visualizzate le card
     */
    public void setPlaylistFlowPane(FlowPane playlistFlowPane) {
        this.playlistFlowPane = playlistFlowPane;
        playlistService.getAllPlaylists().addListener((ListChangeListener<Playlist>) change -> {
            rebuildCards();
        });
    }

    /**
     * Restituisce la playlist attualmente selezionata tramite click su una card.
     *
     * @return playlist selezionata, o {@code null} se nessuna è selezionata
     */
    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    /**
     * Apre un dialog per inserire il nome e crea una nuova playlist tramite
     * {@link PlaylistService#createPlaylist}. In caso di errore mostra un alert.
     * La lista delle card si aggiorna automaticamente tramite il listener.
     */
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
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Elimina la playlist attualmente selezionata tramite
     * {@link PlaylistService#deletePlaylist}. Mostra un errore se nessuna
     * playlist è selezionata. La lista delle card si aggiorna automaticamente.
     */
    @FXML
    public void onDeletePlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }
        playlistService.deletePlaylist(selectedPlaylist);
        selectedPlaylist = null;
    }

    /**
     * Ricostruisce tutte le card nel pannello partendo dalla lista corrente
     * delle playlist. Viene invocato automaticamente dal listener ogni volta
     * che la lista cambia.
     */
    private void rebuildCards() {
        playlistFlowPane.getChildren().clear();
        selectedPlaylist = null;
        for (Playlist p : playlistService.getAllPlaylists()) {
            playlistFlowPane.getChildren().add(createCard(p));
        }
    }

    /**
     * Crea la card visiva per una singola playlist: blocco grigio arrotondato
     * con il nome. Il click sulla card la seleziona evidenziandola.
     *
     * @param playlist playlist da rappresentare
     * @return nodo JavaFX che rappresenta la card
     */
    private Node createCard(Playlist playlist) {
        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(98);

        VBox card = new VBox(nameLabel);
        card.setPrefWidth(110);
        card.setPrefHeight(90);
        card.setAlignment(Pos.BOTTOM_LEFT);
        card.setStyle(styleCard(false));

        card.setOnMouseClicked(e -> {
            resetAllCards();
            card.setStyle(styleCard(true));
            selectedPlaylist = playlist;
        });

        return card;
    }

    /** Rimuove la selezione visiva da tutte le card. */
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
