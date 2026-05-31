package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

/**
 * Controller principale dell'applicazione.
 * Crea l'unica istanza di {@link Library} condivisa tra tutti i servizi,
 * istanzia {@link LibraryService} e {@link PlaylistService} passandola ad entrambi,
 * e coordina {@link TrackListController} e {@link PlaylistController}.
 */
public class MainController {

    private final TrackListController trackListController = new TrackListController();
    private final PlaylistController  playlistController  = new PlaylistController();

    @FXML private TableView<Track> trackTableView;
    @FXML private FlowPane         playlistFlowPane;

    /**
     * Invocato da FXMLLoader al caricamento della vista.
     * Crea la {@link Library} condivisa e la passa ad entrambi i service,
     * così tracce e playlist vivono nello stesso contenitore.
     */
    @FXML
    public void initialize() {
        Library library = new Library();

        LibraryService libraryService = new LibraryService(library);
        trackListController.setLibraryService(libraryService);
        trackListController.setTrackTableView(trackTableView);

        PlaylistService playlistService = new PlaylistService(library);
        playlistController.setPlaylistService(playlistService);
        playlistController.setPlaylistFlowPane(playlistFlowPane);
    }

    /** Delega l'aggiunta di una traccia a {@link TrackListController}. */
    @FXML
    private void onAddTrackButton() {
        trackListController.onAddTrack();
    }

    /** Delega l'eliminazione di una traccia a {@link TrackListController}. */
    @FXML
    private void onDeleteTrackButton() {
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
}
