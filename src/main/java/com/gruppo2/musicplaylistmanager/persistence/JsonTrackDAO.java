package com.gruppo2.musicplaylistmanager.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppo2.musicplaylistmanager.model.Track;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementazione JSON di {@link TrackDAO} tramite Jackson.
 * Serializza e deserializza la lista di tracce su {@code ~/.musicplaylistmanager/tracks.json}.
 */
public class JsonTrackDAO implements TrackDAO {

    static final Path DATA_DIR   = Paths.get(System.getProperty("user.home"), ".musicplaylistmanager");
    static final Path TRACK_FILE = DATA_DIR.resolve("tracks.json");

    final ObjectMapper mapper = new ObjectMapper();

    /**
     * Serializza la lista di tracce su file JSON.
     */
    @Override
    public void save(List<Track> tracks) {
        throw new UnsupportedOperationException("save non ancora implementata");
    }

    /**
     * Deserializza le tracce dal file JSON.
     * Restituisce una lista vuota se il file non esiste o è vuoto.
     */
    @Override
    public List<Track> load() {
        throw new UnsupportedOperationException("load non ancora implementata");
    }
}
