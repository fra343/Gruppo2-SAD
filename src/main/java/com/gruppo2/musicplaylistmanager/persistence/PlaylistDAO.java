package com.gruppo2.musicplaylistmanager.persistence;

import com.gruppo2.musicplaylistmanager.model.Playlist;

import java.util.List;

/**
 * Contratto DAO per la persistenza delle playlist.
 * Le implementazioni concrete decidono il formato di storage (es. JSON, DB).
 */
public interface PlaylistDAO {

    /**
     * Persiste la lista di playlist nel supporto di storage.
     *
     * @param playlists lista di playlist da salvare
     */
    void save(List<Playlist> playlists);

    /**
     * Carica la lista di playlist dal supporto di storage.
     * Restituisce una lista vuota se il file non esiste o è vuoto.
     *
     * @return lista di playlist ricostruite
     */
    List<Playlist> load();
}
