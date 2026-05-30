package com.gruppo2.musicplaylistmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.UUID;

/**
 * Playlist identificata da un nome univoco.
 * Contiene una lista osservabile di tracce, così che la UI possa
 * aggiornarsi automaticamente a ogni aggiunta o rimozione.
 * L'uguaglianza è basata sull'UUID: due playlist con lo stesso nome
 * ma create separatamente non sono mai uguali.
 */
public class Playlist {

    private final UUID id;
    private String name;
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    /**
     * Crea una playlist con il nome indicato e un UUID univoco.
     *
     * @param name nome della playlist (non vuoto)
     */
    public Playlist(String name) {
        this.id   = UUID.randomUUID();
        this.name = name;
    }

    /** @return identificatore UUID immutabile della playlist */
    public UUID getId()   { return id; }

    /** @return nome corrente della playlist */
    public String getName() { return name; }

    /**
     * Aggiorna il nome della playlist.
     *
     * @param name nuovo nome (non vuoto)
     */
    public void setName(String name) { this.name = name; }

    /**
     * Aggiunge una traccia alla playlist solo se non è già presente.
     * Il controllo duplicati usa {@link Track#equals}, basato sull'UUID.
     *
     * @param track traccia da aggiungere
     */
    public void addTrack(Track track) {
        if (!tracks.contains(track)) {
            tracks.add(track);
        }
    }

    /**
     * Rimuove una traccia dalla playlist.
     *
     * @param track traccia da rimuovere
     * @return {@code true} se la traccia era presente ed è stata rimossa,
     *         {@code false} altrimenti
     */
    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    /**
     * Restituisce la lista osservabile delle tracce della playlist.
     * Le modifiche a questa lista si propagano automaticamente alla UI.
     *
     * @return lista osservabile delle tracce
     */
    public ObservableList<Track> getTracks() { return tracks; }

    /**
     * Due playlist sono uguali se e solo se condividono lo stesso UUID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        return id.equals(((Playlist) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** @return nome della playlist */
    @Override
    public String toString() {
        return name;
    }
}
