package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

/*
 * Controller principale: inizializza i servizi condivisi e delega
 * le operazioni a TrackListController e PlaylistController.
 */
public class MainController {

    private final TrackListController trackListController = new TrackListController();

    @FXML private TableView<Track> trackTableView;

    
    private final PlaylistController playlistController = new PlaylistController();
    @FXML private FlowPane playlistFlowPane;

    @FXML
    public void initialize() {
        Library library        = new Library();
        LibraryService service = new LibraryService(library);

        trackListController.setLibraryService(service);
        trackListController.setTrackTableView(trackTableView);

        
        PlaylistService playlistService = new PlaylistService();
        playlistController.setPlaylistService(playlistService);
        playlistController.setPlaylistFlowPane(playlistFlowPane);
    }

    @FXML
    private void onAddTrackButton() {
        trackListController.onAddTrack();
    }

    
    @FXML
    private void onDeleteTrackButton() {
    }

    @FXML
    private void onEditTrackButton() {
        trackListController.onEditTrack();
    }

    
    @FXML
    private void onCreatePlaylistButton() {
        playlistController.onCreatePlaylist();
    }

    @FXML
    private void onDeletePlaylistButton() {
        playlistController.onDeletePlaylist();
    }
}
