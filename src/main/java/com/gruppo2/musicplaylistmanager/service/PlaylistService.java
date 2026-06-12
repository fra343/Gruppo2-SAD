package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Servizio per la gestione delle playlist.
 * Delega la persistenza in memoria alla {@link Library} condivisa, esattamente
 * come {@link LibraryService} fa per le tracce.
 * Applica le regole di business: nome non vuoto, nessun duplicato case-insensitive.
 */
public class PlaylistService {

    private final Library library;

    /**
     * Crea il servizio legandolo alla libreria indicata.
     *
     * @param library istanza condivisa della libreria
     */
    public PlaylistService(Library library) {
        this.library = library;
    }

    /**
     * Crea una nuova playlist con il nome indicato, la aggiunge alla libreria
     * e restituisce l'oggetto creato.
     * Il nome viene normalizzato con {@code trim()} prima del controllo duplicati.
     *
     * @param name nome della playlist
     * @return la {@link Playlist} appena creata e aggiunta alla libreria
     * @throws IllegalArgumentException se il nome è vuoto, blank,
     *                                  o già usato da un'altra playlist (confronto case-insensitive)
     */
    public Playlist createPlaylist(String name) {
        ValidationUtils.requireNonEmpty(name, "Nome playlist");
        boolean duplicate = library.getPlaylists().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Esiste già una playlist con il nome '" + name.trim() + "'.");
        }
        Playlist playlist = new Playlist(name.trim());
        library.addPlaylist(playlist);
        return playlist;
    }

    /**
     * Rimuove la playlist indicata dalla libreria.
     * Se la playlist non è presente non fa nulla.
     *
     * @param playlist playlist da eliminare
     */
    public void deletePlaylist(Playlist playlist) {
        library.removePlaylist(playlist);
    }

    /**
     * Reinserisce nella libreria una playlist precedentemente eliminata,
     * preservandone UUID e tracce.
     * Usato da {@link com.gruppo2.musicplaylistmanager.command.DeletePlaylistCommand#undo()}
     * per ripristinare lo stato precedente senza creare un nuovo oggetto.
     * Non esegue controlli di duplicati: è responsabilità del chiamante
     * assicurarsi che la playlist non sia già presente.
     *
     * @param playlist playlist da ripristinare
     */
    public void restorePlaylist(Playlist playlist) {
        library.addPlaylist(playlist);
    }

    /**
     * Aggiunge una traccia alla playlist indicata.
     * Il controllo duplicati si basa sull'UUID della traccia (via
     * {@link Playlist#addTrack}, che usa {@link Track#equals}).
     * Se la traccia è già presente la playlist rimane invariata e viene
     * lanciata un'eccezione.
     *
     * @param playlist playlist di destinazione
     * @param track    traccia da aggiungere
     * @throws IllegalArgumentException se la traccia è già presente nella playlist
     */
    public void addTrackToPlaylist(Playlist playlist, Track track) {
        if (playlist.getTracks().contains(track)) {
            throw new IllegalArgumentException(
                    "La traccia \"" + track.getTitle() + "\" è già presente nella playlist.");
        }
        playlist.addTrack(track);
    }

    /**
     * Rimuove una traccia dalla playlist indicata.
     * La traccia rimane disponibile nella libreria globale.
     * Se la traccia non è presente nella playlist il metodo non produce effetti.
     *
     * @param playlist playlist da cui rimuovere
     * @param track    traccia da rimuovere
     */
    public void removeTrackFromPlaylist(Playlist playlist, Track track) {
        playlist.removeTrack(track);
    }

    /**
     * Reinserisce una traccia in una playlist precedentemente eliminata, senza
     * eseguire il controllo duplicati di business (già applicato all'aggiunta originale).
     * Usato da {@link com.gruppo2.musicplaylistmanager.command.RemoveTrackFromLibraryCommand#undo()}
     * per ripristinare lo stato precedente passando per il Service layer anziché
     * accedere direttamente al Model.
     *
     * @param playlist playlist di destinazione
     * @param track    traccia da ripristinare
     */
    public void restoreTrackToPlaylist(Playlist playlist, Track track) {
        playlist.addTrack(track);
    }

    /**
     * Restituisce la lista osservabile di tutte le playlist della libreria.
     * Il pannello delle card nella UI è direttamente agganciato a questa lista.
     *
     * @return lista osservabile delle playlist
     */
    public ObservableList<Playlist> getAllPlaylists() {
        return library.getPlaylists();
    }

    /**
     * Crea una nuova playlist con il nome indicato, vi aggiunge tutte le tracce
     * fornite e la inserisce nella libreria.
     * Usato dai controller di generazione automatica: ognuno interroga la Library
     * con {@code findByGenre}/{@code findByYear}/{@code findByTag} e passa il risultato
     * a questo metodo.
     *
     * @param name   nome della playlist da generare
     * @param tracks tracce ottenute dal criterio di ricerca; non deve essere vuota
     * @return la nuova playlist aggiunta alla libreria
     * @throws IllegalArgumentException se la lista di tracce è vuota
     */
    public Playlist generateAutoPlaylist(String name, List<Track> tracks) {
        if (tracks.isEmpty()) {
            throw new IllegalArgumentException("Nessuna traccia trovata per il criterio selezionato.");
        }
        Playlist playlist = new Playlist(name);
        tracks.forEach(playlist::addTrack);
        library.addPlaylist(playlist);
        return playlist;
    }
}
