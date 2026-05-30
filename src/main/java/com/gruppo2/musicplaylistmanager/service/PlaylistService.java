package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


 //Servizio per la gestione delle playlist.
 //Gestisce la creazione (con controllo nomi duplicati) e l'eliminazione.

public class PlaylistService {

    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    //Crea una nuova playlist con il nome indicato e la aggiunge alla collezione.
    //Lancia IllegalArgumentException se il nome è vuoto o già esistente.
    public void createPlaylist(String name) {
        ValidationUtils.requireNonEmpty(name, "Nome playlist");
        // Controllo nomi duplicati (case-insensitive)
        boolean duplicate = playlists.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Esiste già una playlist con il nome '" + name.trim() + "'.");
        }
        playlists.add(new Playlist(name.trim()));
    }


    //Elimina la playlist indicata dalla collezione.
    //Se la playlist non è presente non fa nulla.
    
    public void deletePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }

    //Restituisce la lista osservabile di tutte le playlist.
    public ObservableList<Playlist> getAllPlaylists() {
        return playlists;
    }
}
