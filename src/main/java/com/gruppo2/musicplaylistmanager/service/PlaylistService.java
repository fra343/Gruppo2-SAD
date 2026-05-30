package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servizio per la gestione delle playlist.
 * Espone le operazioni di creazione ed eliminazione, applicando le regole
 * di business (nome non vuoto, nessun duplicato case-insensitive).
 */
public class PlaylistService {

    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    /**
     * Crea una nuova playlist con il nome indicato e la aggiunge alla collezione.
     * Il nome viene normalizzato con {@code trim()} prima del controllo duplicati.
     *
     * @param name nome della playlist
     * @throws IllegalArgumentException se il nome è vuoto, blank,
     *                                  o già usato da un'altra playlist (confronto case-insensitive)
     */
    public void createPlaylist(String name) {
        ValidationUtils.requireNonEmpty(name, "Nome playlist");
        boolean duplicate = playlists.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Esiste già una playlist con il nome '" + name.trim() + "'.");
        }
        playlists.add(new Playlist(name.trim()));
    }

    /**
     * Rimuove la playlist indicata dalla collezione.
     * Se la playlist non è presente non fa nulla.
     *
     * @param playlist playlist da eliminare
     */
    public void deletePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }

    /**
     * Restituisce la lista osservabile di tutte le playlist.
     * Il pannello delle card nella UI è direttamente agganciato a questa lista.
     *
     * @return lista osservabile delle playlist
     */
    public ObservableList<Playlist> getAllPlaylists() {
        return playlists;
    }
}
