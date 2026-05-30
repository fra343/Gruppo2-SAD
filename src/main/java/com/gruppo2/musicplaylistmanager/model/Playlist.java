package com.gruppo2.musicplaylistmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.UUID;


//Playlist  identificata da un nome univoco.
//Contiene una lista osservabile di tracce della libreria.

public class Playlist {

    private final UUID id;
    private String name;
    // Lista osservabile: la UI si aggiorna automaticamente ad ogni modifica
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();

    public Playlist(String name) {
        this.id   = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId()   { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    //Aggiunge una traccia alla playlist se non è già presente.
    public void addTrack(Track track) {
        if (!tracks.contains(track)) {
            tracks.add(track);
        }
    }

    //Rimuove una traccia dalla playlist, restituisce true se era presente
    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    public ObservableList<Track> getTracks() { return tracks; }

    // Uguaglianza basata sull'id: due playlist con lo stesso nome ma create separatamente non sono uguali
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

    @Override
    public String toString() { 
        return name; 
    }
}
