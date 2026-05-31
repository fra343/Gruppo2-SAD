package com.gruppo2.musicplaylistmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contenitore globale dell'intera libreria musicale: tracce e playlist.
 * Entrambe le collezioni sono osservabili così che la UI si aggiorni
 * automaticamente a ogni modifica senza chiamate esplicite dai controller.
 */
public class Library {

    private final ObservableList<Track>    tracks    = FXCollections.observableArrayList();
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    /** Aggiunge una traccia alla libreria. */
    public void addTrack(Track track) {
        tracks.add(track);
    }

    /**
     * Rimuove una traccia dalla libreria.
     *
     * @return {@code true} se la traccia era presente ed è stata rimossa
     */
    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    /** Restituisce la lista osservabile delle tracce. */
    public ObservableList<Track> getTracks() {
        return tracks;
    }

    /** Aggiunge una playlist alla libreria. */
    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }

    /**
     * Rimuove una playlist dalla libreria.
     *
     * @return {@code true} se la playlist era presente ed è stata rimossa
     */
    public boolean removePlaylist(Playlist playlist) {
        return playlists.remove(playlist);
    }

    /** Restituisce la lista osservabile delle playlist. */
    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }
}