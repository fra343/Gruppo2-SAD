package com.gruppo2.musicplaylistmanager.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppo2.musicplaylistmanager.model.Playlist;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementazione JSON di {@link PlaylistDAO} tramite Jackson.
 * Serializza e deserializza la lista di playlist su {@code ~/.musicplaylistmanager/playlists.json}.
 */
public class JsonPlaylistDAO implements PlaylistDAO {

    static final Path DATA_DIR      = Paths.get(System.getProperty("user.home"), ".musicplaylistmanager");
    static final Path PLAYLIST_FILE = DATA_DIR.resolve("playlists.json");

    final ObjectMapper mapper = new ObjectMapper();

    /**
     * Serializza la lista di playlist su file JSON.
     */
    @Override
    public void save(List<Playlist> playlists) {
        throw new UnsupportedOperationException("save non ancora implementata");
    }

    /**
     * Deserializza le playlist dal file JSON.
     * Restituisce una lista vuota se il file non esiste o è vuoto.
     */
    @Override
    public List<Playlist> load() {
        throw new UnsupportedOperationException("load non ancora implementata");
    }
}
