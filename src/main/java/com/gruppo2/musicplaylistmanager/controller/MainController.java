package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.command.CommandHistory;
import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * Controller principale dell'applicazione.
 * Crea l'unica istanza di {@link Library} e di {@link CommandHistory}
 * condivise tra tutti i servizi e i controller, istanzia
 * {@link LibraryService} e {@link PlaylistService} passando loro la libreria,
 * e coordina {@link TrackListController} e {@link PlaylistController}.
 */
public class MainController {

    private final TrackListController trackListController = new TrackListController();
    private final PlaylistController  playlistController  = new PlaylistController();

    @FXML private TableView<Track>           trackTableView;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, String> genreColumn;
    @FXML private TableColumn<Track, String> yearColumn;
    @FXML private TableColumn<Track, String> durationColumn;
    @FXML private FlowPane                   playlistFlowPane;
    @FXML private VBox                       playlistDetailPane;
    @FXML private Label                      playlistDetailLabel;
    @FXML private ListView<Track>            playlistTrackListView;

    /**
     * Invocato da FXMLLoader al caricamento della vista.
     * Crea {@link Library} e {@link CommandHistory} condivise,
     * configura i service, il binding della TableView e
     * passa le dipendenze ai sub-controller.
     */
    @FXML
    public void initialize() {
        Library        library        = new Library();
        CommandHistory commandHistory = new CommandHistory();

        LibraryService libraryService = new LibraryService(library);
        trackListController.setLibraryService(libraryService);
        trackListController.setCommandHistory(commandHistory);
        trackListController.setTrackTableView(trackTableView);

        titleColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getAuthor()));
        genreColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getGenre()));
        yearColumn.setCellValueFactory(
                data -> new SimpleStringProperty(String.valueOf(data.getValue().getYear())));
        durationColumn.setCellValueFactory(data -> {
            int s = data.getValue().getDuration();
            return new SimpleStringProperty(String.format("%d:%02d", s / 60, s % 60));
        });
        trackTableView.setItems(libraryService.getAllTracks());

        PlaylistService playlistService = new PlaylistService(library);
        playlistController.setPlaylistService(playlistService);
        playlistController.setCommandHistory(commandHistory);
        playlistController.setLibraryTableView(trackTableView);
        playlistController.setPlaylistFlowPane(playlistFlowPane);
        playlistController.setPlaylistDetail(
                playlistDetailPane, playlistDetailLabel, playlistTrackListView);
    }

    /** Delega l'aggiunta di una traccia a {@link TrackListController}. */
    @FXML
    private void onAddTrackButton() {
        trackListController.onAddTrack();
    }

    /** Delega l'eliminazione di una traccia a {@link TrackListController}. */
    @FXML
    private void onDeleteTrackButton() {
        trackListController.onDeleteTrack();
    }

    /** Delega la modifica di una traccia a {@link TrackListController}. */
    @FXML
    private void onEditTrackButton() {
        trackListController.onEditTrack();
    }

    /** Delega la creazione di una playlist a {@link PlaylistController}. */
    @FXML
    private void onCreatePlaylistButton() {
        playlistController.onCreatePlaylist();
    }

    /** Delega l'eliminazione di una playlist a {@link PlaylistController}. */
    @FXML
    private void onDeletePlaylistButton() {
        playlistController.onDeletePlaylist();
    }

    /** Delega l'aggiunta della traccia selezionata alla playlist a {@link PlaylistController}. */
    @FXML
    private void onAddToPlaylistButton() {
        playlistController.onAddToPlaylist();
    }

    /** Delega la rimozione della traccia selezionata dalla playlist a {@link PlaylistController}. */
    @FXML
    private void onRemoveFromPlaylistButton() {
        playlistController.onRemoveFromPlaylist();
    }
}
