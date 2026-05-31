package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.ObservableList;

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
     * Crea una nuova playlist con il nome indicato e la aggiunge alla libreria.
     * Il nome viene normalizzato con {@code trim()} prima del controllo duplicati.
     *
     * @param name nome della playlist
     * @throws IllegalArgumentException se il nome è vuoto, blank,
     *                                  o già usato da un'altra playlist (confronto case-insensitive)
     */
    public void createPlaylist(String name) {
        ValidationUtils.requireNonEmpty(name, "Nome playlist");
        boolean duplicate = library.getPlaylists().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Esiste già una playlist con il nome '" + name.trim() + "'.");
        }
        library.addPlaylist(new Playlist(name.trim()));
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
     * Restituisce la lista osservabile di tutte le playlist della libreria.
     * Il pannello delle card nella UI è direttamente agganciato a questa lista.
     *
     * @return lista osservabile delle playlist
     */
    public ObservableList<Playlist> getAllPlaylists() {
        return library.getPlaylists();
    }
}
