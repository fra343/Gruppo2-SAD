package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

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
     *                                  la durata è minore o uguale a 0, o l'anno è fuori range
     */
    public void addTrack(Track track) {
        ValidationUtils.requireNonEmpty(track.getTitle(), "Titolo");
        ValidationUtils.requireNonEmpty(track.getAuthor(), "Autore");
        ValidationUtils.requireNonEmpty(track.getGenre(), "Genere");
        ValidationUtils.requireValidDuration(track.getDuration());
        ValidationUtils.requireValidYear(track.getYear());
        boolean isDuplicate = library.getTracks().stream().anyMatch(t ->
            t.getTitle().equalsIgnoreCase(track.getTitle()) &&
            t.getAuthor().equalsIgnoreCase(track.getAuthor()) &&
            t.getGenre().equalsIgnoreCase(track.getGenre())
        );
        if (isDuplicate) {
            throw new IllegalArgumentException(
                "Esiste già una traccia con lo stesso titolo, autore e genere.");
        }
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

    /**
     * Restituisce le tracce della libreria del genere indicato, delegando a
     * {@link Library#findByGenre(String)} (confronto case-insensitive).
     * Usato dalla generazione automatica di playlist per genere (US-14a).
     *
     * @param genre genere musicale da cercare
     * @return lista (eventualmente vuota) delle tracce corrispondenti
     */
    public List<Track> findByGenre(String genre) {
        return library.findByGenre(genre);
    }

    /**
     * Restituisce le tracce della libreria pubblicate nell'anno indicato,
     * delegando a {@link Library#findByYear(int)}.
     * Usato dalla generazione automatica di playlist per anno (US-14b).
     *
     * @param year anno di pubblicazione
     * @return lista (eventualmente vuota) delle tracce corrispondenti
     */
    public List<Track> findByYear(int year) {
        return library.findByYear(year);
    }

    /**
     * Reinserisce nella libreria una traccia precedentemente eliminata,
     * preservandone UUID e dati originali.
     * Usato da {@link com.gruppo2.musicplaylistmanager.command.RemoveTrackFromLibraryCommand#undo()}
     * per ripristinare lo stato precedente senza ri-eseguire la validazione di business
     * (già applicata al momento dell'aggiunta originale).
     * Non esegue controlli di duplicati: è responsabilità del chiamante
     * assicurarsi che la traccia non sia già presente.
     *
     * @param track traccia da ripristinare
     */
    public void restoreTrack(Track track) {
        library.addTrack(track);
    }

    /**
     * Restituisce le playlist della libreria che contengono la traccia indicata.
     * Usato da {@link com.gruppo2.musicplaylistmanager.command.RemoveTrackFromLibraryCommand#undo()}
     * per ripristinare la traccia nelle playlist originarie dopo un undo.
     *
     * @param track traccia di cui cercare le playlist contenenti
     * @return lista (snapshot) delle playlist che contengono la traccia
     */
    public List<Playlist> getPlaylistsContaining(Track track) {
        return library.getPlaylists().stream()
                .filter(p -> p.getTracks().contains(track))
                .collect(Collectors.toList());
    }
}