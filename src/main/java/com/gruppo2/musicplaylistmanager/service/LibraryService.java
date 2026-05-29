package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.ObservableList;

/**
 * Servizio che gestisce la libreria delle tracce.
 * Fa da intermediario tra i controller e il Model.
 */
public class LibraryService {

    private final Library library;

    public LibraryService(Library library) {
        this.library = library;
    }

    /**
     * Valida i dati di una traccia e, se corretti, la aggiunge alla libreria.
     * Se un valore non è valido lancia IllegalArgumentException.
     */
    public void addTrack(Track track) {
        ValidationUtils.requireNonEmpty(track.getTitle(), "Titolo");
        ValidationUtils.requireNonEmpty(track.getAuthor(), "Autore");
        ValidationUtils.requireNonEmpty(track.getGenre(), "Genere");
        ValidationUtils.requireValidDuration(track.getDuration());
        ValidationUtils.requireValidYear(track.getYear());
        library.addTrack(track);
    }


    public void removeTrack(Track track) {
        
    }

    public ObservableList<Track> getAllTracks() {
        
        return library.getTracks();
    }
}