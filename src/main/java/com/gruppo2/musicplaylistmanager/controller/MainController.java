package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import javafx.fxml.FXML;

/**
 * Controller principale: inizializza i servizi e coordina i controller delle viste.
 */
public class MainController {

    private final TrackListController trackListController = new TrackListController();

    @FXML
    public void initialize() {
        // Model e service condivisi dell'applicazione.
        Library library = new Library();
        LibraryService libraryService = new LibraryService(library);
        trackListController.setLibraryService(libraryService);
    }

    /** Click sul pulsante "Aggiungi traccia": delega al controller delle tracce. */
    @FXML
    private void onAddTrackButton() {
        trackListController.onAddTrack();
    }
}