package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller della Home page.
 * Mostra le tracce e le playlist più ascoltate, ordinate per numero di riproduzioni decrescente.
 */
public class HomeController {

    @FXML private ListView<Track>    topTracksListView;
    @FXML private ListView<Playlist> topPlaylistsListView;
    @FXML private Label              noTracksLabel;
    @FXML private Label              noPlaylistsLabel;

    private Library         library;
    private PlaybackService playbackService;

    /**
     * Inietta le dipendenze necessarie al controller.
     *
     * @param library         libreria condivisa con l'applicazione
     * @param playbackService servizio di riproduzione da cui leggere i conteggi
     */
    public void setDependencies(Library library, PlaybackService playbackService) {
        this.library         = library;
        this.playbackService = playbackService;
    }

    /**
     * Aggiorna le liste ordinando per conteggio decrescente.
     */
    public void refresh() {
    }
}
