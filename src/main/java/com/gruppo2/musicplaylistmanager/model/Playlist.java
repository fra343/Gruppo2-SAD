package com.gruppo2.musicplaylistmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Playlist identificata da un nome univoco.
 * Contiene una lista osservabile di tracce, così che la UI possa
 * aggiornarsi automaticamente a ogni aggiunta o rimozione.
 * L'uguaglianza è basata sull'UUID: due playlist con lo stesso nome
 * ma create separatamente non sono mai uguali.
 * <p>
 * La serializzazione Jackson usa il costruttore annotato con {@link JsonCreator}.
 * Per la serializzazione, {@link #getTracksForJson()} espone la lista come
 * {@code List} ordinaria, evitando che Jackson tenti di istanziare {@code ObservableList}.
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

    /**
     * Ricostruisce la playlist con UUID e tracce preesistenti dalla deserializzazione JSON.
     */
    @JsonCreator
    public Playlist(
            @JsonProperty("id")     UUID        id,
            @JsonProperty("name")   String      name,
            @JsonProperty("tracks") List<Track> tracks) {
        this.id   = id != null ? id : UUID.randomUUID();
        this.name = name;
        if (tracks != null) {
            this.tracks.addAll(tracks);
        }
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
     * Sposta la traccia dalla posizione {@code fromIndex} alla posizione {@code toIndex}.
     * Entrambi gli indici devono essere validi; se sono uguali il metodo non produce effetti.
     * Poiché la lista è un'ObservableList, la UI si aggiorna automaticamente.
     *
     * @param fromIndex posizione corrente della traccia (0-based)
     * @param toIndex   posizione di destinazione (0-based)
     */
    public void moveTrack(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;
        if (fromIndex < 0 || fromIndex >= tracks.size()) return;
        if (toIndex   < 0 || toIndex   >= tracks.size()) return;
        Track t = tracks.remove(fromIndex);
        tracks.add(toIndex, t);
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
    @JsonIgnore
    public ObservableList<Track> getTracks() { return tracks; }

    /**
     * Espone le tracce come {@code List} ordinaria per la serializzazione Jackson.
     * Usato come getter JSON al posto di {@link #getTracks()} per evitare che
     * Jackson tenti di istanziare {@code ObservableList} durante la deserializzazione.
     *
     * @return copia snapshot delle tracce come ArrayList
     */
    @JsonProperty("tracks")
    public List<Track> getTracksForJson() { return new ArrayList<>(tracks); }

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
