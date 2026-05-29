package com.gruppo2.musicplaylistmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contenitore globale delle tracce della libreria.
 * La lista è osservabile così che la UI possa aggiornarsi da sola.
 */
public class Library {

    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    /** Aggiunge una traccia alla libreria. */
    public void addTrack(Track track) {
        tracks.add(track);
    }

    /** Restituisce la lista osservabile delle tracce. */
    public ObservableList<Track> getTracks() {
        return tracks;
    }
}