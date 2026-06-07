package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.command.AddTrackToPlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.CommandHistory;
import com.gruppo2.musicplaylistmanager.command.CreatePlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.DeletePlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.RemoveTrackFromPlaylistCommand;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Controller per la gestione delle playlist: creazione, eliminazione,
 * aggiunta e rimozione di tracce da una playlist selezionata.
 * <p>
 * Visualizza le playlist come card cliccabili nel pannello destro e,
 * quando una card è selezionata, mostra un pannello di dettaglio con
 * le tracce contenute e i pulsanti di aggiunta/rimozione.
 * </p>
 * Le operazioni di modifica (aggiunta e rimozione di tracce) vengono
 * incapsulate in oggetti {@link com.gruppo2.musicplaylistmanager.command.Command}
 * e affidate alla {@link CommandHistory} condivisa, in modo che l'undo
 * possa essere abilitato in US-12 senza modificare questo controller.
 */
public class PlaylistController {

    private static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");

    private PlaylistService  playlistService;
    private PlaybackService  playbackService;
    private CommandHistory   commandHistory;
    private FlowPane         playlistFlowPane;
    private TableView<Track> libraryTableView;
    private VBox             playlistDetailPane;
    private Label            playlistDetailLabel;
    private ListView<Track>  playlistTrackListView;

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
     * Inietta il servizio di riproduzione condiviso, necessario per notificare
     * la rimozione di una traccia durante la riproduzione .
     *
     * @param playbackService servizio di riproduzione condiviso
     */
    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    /**
     * Inietta la {@link CommandHistory} condivisa, usata per registrare
     * le operazioni di aggiunta e rimozione di tracce.
     *
     * @param commandHistory storia dei comandi condivisa con gli altri controller
     */
    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * Inietta la TableView della libreria, necessaria per leggere
     * la traccia selezionata al momento dell'aggiunta a una playlist.
     *
     * @param libraryTableView tabella delle tracce della libreria
     */
    public void setLibraryTableView(TableView<Track> libraryTableView) {
        this.libraryTableView = libraryTableView;
    }

    /**
     * Collega il {@link FlowPane} al controller e registra un listener
     * sull'ObservableList delle playlist: ogni modifica alla lista
     * ricostruisce automaticamente le card nel pannello.
     *
     * @param playlistFlowPane pannello in cui vengono visualizzate le card
     */
    public void setPlaylistFlowPane(FlowPane playlistFlowPane) {
        this.playlistFlowPane = playlistFlowPane;
        playlistService.getAllPlaylists().addListener(
                (ListChangeListener<Playlist>) change -> rebuildCards());
    }

    /**
     * Inietta i componenti del pannello di dettaglio della playlist selezionata:
     * il contenitore (inizialmente nascosto), l'etichetta con il nome della
     * playlist e la ListView delle tracce contenute.
     *
     * @param pane     VBox contenitore del dettaglio
     * @param label    Label che mostra "Tracce in: &lt;nome&gt;"
     * @param listView ListView collegata all'ObservableList della playlist
     */
    public void setPlaylistDetail(VBox pane, Label label, ListView<Track> listView) {
        this.playlistDetailPane    = pane;
        this.playlistDetailLabel   = label;
        this.playlistTrackListView = listView;

        listView.setCellFactory(view -> new ListCell<>() {
            private final Button playBtn = new Button("▶");
            private final InvalidationListener hl = obs -> updateHighlight();

            {
                playBtn.getStyleClass().add("btn-play-inline");
                playBtn.setOnAction(e -> {
                    if (getItem() != null && selectedPlaylist != null)
                        playbackService.play(selectedPlaylist, getItem());
                });
                playbackService.currentTrackProperty()
                        .addListener(new WeakInvalidationListener(hl));
            }

            @Override
            protected void updateItem(Track item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setGraphic(playBtn);
                }
                updateHighlight();
            }

            private void updateHighlight() {
                Track current = playbackService.currentTrackProperty().get();
                pseudoClassStateChanged(CURRENT,
                        !isEmpty() && getItem() != null && getItem().equals(current));
            }
        });
    }

    /**
     * @return la playlist attualmente selezionata, o {@code null} se nessuna
     *         card è selezionata
     */
    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    /**
     * Apre un dialog per inserire il nome e crea una nuova playlist tramite
     * {@link PlaylistService#createPlaylist}. In caso di errore mostra un alert.
     * La lista delle card si aggiorna automaticamente tramite il listener.
     */
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
                commandHistory.push(new CreatePlaylistCommand(playlistService, nameField.getText()));
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Elimina la playlist attualmente selezionata tramite
     * {@link PlaylistService#deletePlaylist}, previa conferma dell'utente.
     * Mostra un errore se nessuna playlist è selezionata.
     * La lista delle card si aggiorna automaticamente tramite il listener.
     */
    public void onDeletePlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Eliminare la playlist \"" + selectedPlaylist.getName() + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (playbackService != null && selectedPlaylist.equals(playbackService.getCurrentPlaylist())) {
                playbackService.notifyPlaylistRemoved(selectedPlaylist);
            }
            commandHistory.push(new DeletePlaylistCommand(playlistService, selectedPlaylist));
            selectedPlaylist = null;
        }
    }

    /**
     * Aggiunge la traccia selezionata nella libreria alla playlist corrente.
     * L'operazione viene incapsulata in un {@link AddTrackToPlaylistCommand}
     * e affidata alla {@link CommandHistory}.
     * Mostra un errore se nessuna playlist o nessuna traccia è selezionata,
     * o se la traccia è già presente nella playlist.
     */
    public void onAddToPlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona prima una playlist.");
            return;
        }
        Track selectedTrack = libraryTableView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            showError("Seleziona una traccia dalla libreria.");
            return;
        }
        try {
            commandHistory.push(
                    new AddTrackToPlaylistCommand(playlistService, playbackService, selectedPlaylist, selectedTrack));
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Rimuove la traccia selezionata nel pannello di dettaglio dalla playlist
     * corrente. L'operazione viene incapsulata in un
     * {@link RemoveTrackFromPlaylistCommand} e affidata alla {@link CommandHistory}.
     * Mostra un errore se nessuna playlist o nessuna traccia è selezionata.
     */
    public void onRemoveFromPlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona prima una playlist.");
            return;
        }
        Track selectedTrack = playlistTrackListView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            showError("Seleziona una traccia dalla playlist da rimuovere.");
            return;
        }
        // Notifica prima della rimozione: la traccia è ancora nella playlist,
        // quindi la strategia può calcolarne la posizione corretta.
        if (playbackService != null && selectedPlaylist.equals(playbackService.getCurrentPlaylist())) {
            playbackService.notifyTrackRemoved(selectedTrack);
        }
        commandHistory.push(
                new RemoveTrackFromPlaylistCommand(playlistService, selectedPlaylist, selectedTrack));
    }

    /**
     * Ricostruisce tutte le card nel pannello partendo dalla lista corrente
     * delle playlist, azzera la selezione e nasconde il pannello di dettaglio.
     * Viene invocato automaticamente dal listener ogni volta che la lista cambia.
     */
    private void rebuildCards() {
        playlistFlowPane.getChildren().clear();
        selectedPlaylist = null;
        hidePlaylistDetail();
        for (Playlist p : playlistService.getAllPlaylists()) {
            playlistFlowPane.getChildren().add(createCard(p));
        }
    }

    /**
     * Crea la card visiva per una singola playlist.
     * Il click seleziona la card, la evidenzia e mostra il pannello di dettaglio.
     *
     * @param playlist playlist da rappresentare
     * @return nodo JavaFX che rappresenta la card
     */
    private Node createCard(Playlist playlist) {
        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"
                + " -fx-text-fill: #333333; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(98);

        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("btn-play-inline");
        playBtn.setOnAction(e -> playbackService.play(playlist));

        HBox topBar = new HBox(playBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(topBar, spacer, nameLabel);
        card.setPrefWidth(110);
        card.setPrefHeight(90);
        card.setStyle(styleCard(false));

        card.setOnMouseClicked(e -> {
            if (playlist.equals(selectedPlaylist)) {
                card.setStyle(styleCard(false));
                selectedPlaylist = null;
                hidePlaylistDetail();
            } else {
                resetAllCards();
                card.setStyle(styleCard(true));
                selectedPlaylist = playlist;
                showPlaylistDetail(playlist);
            }
        });

        return card;
    }

    /**
     * Mostra il pannello di dettaglio per la playlist indicata:
     * aggiorna l'etichetta e collega la ListView all'ObservableList della playlist.
     * Il binding diretto fa sì che la vista si aggiorni automaticamente
     * a ogni aggiunta o rimozione di tracce.
     *
     * @param playlist playlist di cui mostrare il contenuto
     */
    private void showPlaylistDetail(Playlist playlist) {
        if (playlistDetailPane == null) return;
        playlistDetailLabel.setText("Tracce in: " + playlist.getName());
        playlistTrackListView.setItems(playlist.getTracks());
        playlistDetailPane.setVisible(true);
        playlistDetailPane.setManaged(true);
    }

    /**
     * Nasconde il pannello di dettaglio e scollega la ListView
     * dall'eventuale ObservableList precedente.
     */
    private void hidePlaylistDetail() {
        if (playlistDetailPane == null) return;
        playlistDetailPane.setVisible(false);
        playlistDetailPane.setManaged(false);
        playlistTrackListView.setItems(null);
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
                ? base + " -fx-background-color: #C8C8C8;"
                        + " -fx-border-color: #888; -fx-border-radius: 8; -fx-border-width: 2;"
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
