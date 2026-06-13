package com.gruppo2.musicplaylistmanager.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppo2.musicplaylistmanager.model.Track;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JSON di {@link TrackDAO} tramite Jackson.
 * Serializza e deserializza la lista di tracce su {@code ~/.musicplaylistmanager/tracks.json}.
 * <p>
 * Il costruttore package-private {@link #JsonTrackDAO(Path)} permette ai test
 * di iniettare un path temporaneo senza toccare la home dell'utente.
 */
public class JsonTrackDAO implements TrackDAO {

    static final Path DATA_DIR   = Paths.get(System.getProperty("user.home"), ".musicplaylistmanager");
    static final Path TRACK_FILE = DATA_DIR.resolve("tracks.json");

    private final Path file;
    final ObjectMapper mapper = new ObjectMapper();

    /** Costruttore di produzione: usa {@code ~/.musicplaylistmanager/tracks.json}. */
    public JsonTrackDAO() {
        this.file = TRACK_FILE;
    }

    /** Costruttore per i test: consente di iniettare un path alternativo. */
    JsonTrackDAO(Path file) {
        this.file = file;
    }

    /**
     * Serializza la lista di tracce su file JSON (pretty-printed).
     * Crea la directory padre se non esiste.
     *
     * @param tracks lista di tracce da persistere
     * @throws RuntimeException se la scrittura su disco fallisce
     */
    @Override
    public void save(List<Track> tracks) {
        try {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), tracks);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile salvare le tracce: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializza le tracce dal file JSON.
     * Restituisce una lista vuota se il file non esiste o ha dimensione zero.
     *
     * @return lista di tracce ricostruite, mai {@code null}
     * @throws RuntimeException se il file esiste ma non contiene JSON valido
     */
    @Override
    public List<Track> load() {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            if (Files.size(file) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file.toFile(), new TypeReference<List<Track>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare le tracce: " + e.getMessage(), e);
        }
    }
}
