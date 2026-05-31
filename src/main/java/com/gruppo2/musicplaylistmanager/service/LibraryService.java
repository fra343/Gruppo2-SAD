package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.ObservableList;

/**
 * Servizio che gestisce la libreria delle tracce musicali.
 * Fa da intermediario tra i controller e il Model: i controller non accedono
 * mai direttamente a {@link Library}, ma delegano a questo servizio.
 */
public class LibraryService {

    private final Library library;

    /**
     * Crea il servizio legandolo alla libreria indicata.
     *
     * @param library istanza condivisa della libreria
     */
    public LibraryService(Library library) {
        this.library = library;
    }

    /**
     * Valida i dati della traccia e, se corretti, la aggiunge alla libreria.
     * La validazione è atomica: se anche un solo campo non è valido la libreria
     * resta invariata e viene lanciata un'eccezione.
     *
     * @param track traccia da aggiungere
     * @throws IllegalArgumentException se titolo, autore o genere sono vuoti,
     *                                  la durata è <= 0, o l'anno è fuori range
     */
    public void addTrack(Track track) {
        ValidationUtils.requireNonEmpty(track.getTitle(), "Titolo");
        ValidationUtils.requireNonEmpty(track.getAuthor(), "Autore");
        ValidationUtils.requireNonEmpty(track.getGenre(), "Genere");
        ValidationUtils.requireValidDuration(track.getDuration());
        ValidationUtils.requireValidYear(track.getYear());
        library.addTrack(track);
    }

    /**
     * Valida i nuovi valori e aggiorna i campi della traccia.
     * Tutta la validazione avviene prima di toccare i campi: se un valore
     * non è valido la traccia resta invariata.
     *
     * @param track    traccia da aggiornare
     * @param title    nuovo titolo
     * @param author   nuovo autore
     * @param duration nuova durata in secondi
     * @param genre    nuovo genere
     * @param year     nuovo anno
     * @throws IllegalArgumentException se uno dei valori non supera la validazione
     */
    public void updateTrack(Track track, String title, String author,
                            int duration, String genre, int year) {
        ValidationUtils.requireNonEmpty(title, "Titolo");
        ValidationUtils.requireNonEmpty(author, "Autore");
        ValidationUtils.requireNonEmpty(genre, "Genere");
        ValidationUtils.requireValidDuration(duration);
        ValidationUtils.requireValidYear(year);
        track.setTitle(title);
        track.setAuthor(author);
        track.setDuration(duration);
        track.setGenre(genre);
        track.setYear(year);
    }

    /**
     * Rimuove la traccia dalla libreria e da tutte le playlist che la contengono.
     * La propagazione avviene prima della rimozione dalla libreria, così la
     * traccia è ancora raggiungibile durante l'iterazione sulle playlist.
     * Se la traccia non è presente nella libreria il metodo non produce effetti.
     *
     * @param track traccia da rimuovere
     */
    public void removeTrack(Track track) {
        library.getPlaylists().forEach(p -> p.removeTrack(track));
        library.removeTrack(track);
    }

    /**
     * Restituisce la lista osservabile di tutte le tracce della libreria.
     * La TableView della UI è direttamente agganciata a questa lista e si
     * aggiorna automaticamente a ogni modifica.
     *
     * @return lista osservabile delle tracce
     */
    public ObservableList<Track> getAllTracks() {
        return library.getTracks();
    }
}