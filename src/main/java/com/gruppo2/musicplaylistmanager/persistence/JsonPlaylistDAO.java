package com.gruppo2.musicplaylistmanager.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruppo2.musicplaylistmanager.model.Playlist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione JSON di {@link PlaylistDAO} tramite Jackson.
 * Serializza e deserializza la lista di playlist su {@code ~/.musicplaylistmanager/playlists.json}.
 * Ogni playlist incorpora le proprie tracce come oggetti JSON annidati; al caricamento
 * i riferimenti vengono risolti verso le istanze della libreria dal chiamante
 * (vedi {@code MainController.initialize()}).
 * <p>
 * Il costruttore package-private {@link #JsonPlaylistDAO(Path)} permette ai test
 * di iniettare un path temporaneo senza toccare la home dell'utente.
 */
public class JsonPlaylistDAO implements PlaylistDAO {

    static final Path DATA_DIR      = Paths.get(System.getProperty("user.home"), ".musicplaylistmanager");
    static final Path PLAYLIST_FILE = DATA_DIR.resolve("playlists.json");

    private final Path file;
    final ObjectMapper mapper = new ObjectMapper();

    /** Costruttore di produzione: usa {@code ~/.musicplaylistmanager/playlists.json}. */
    public JsonPlaylistDAO() {
        this.file = PLAYLIST_FILE;
    }

    /** Costruttore per i test: consente di iniettare un path alternativo. */
    JsonPlaylistDAO(Path file) {
        this.file = file;
    }

    /**
     * Serializza la lista di playlist su file JSON (pretty-printed).
     * Crea la directory padre se non esiste.
     * Le tracce di ogni playlist sono embedded nel JSON tramite {@link Playlist#getTracksForJson()}.
     *
     * @param playlists lista di playlist da persistere
     * @throws RuntimeException se la scrittura su disco fallisce
     */
    @Override
    public void save(List<Playlist> playlists) {
        try {
            Files.createDirectories(file.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), playlists);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile salvare le playlist: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializza le playlist dal file JSON.
     * Restituisce una lista vuota se il file non esiste o ha dimensione zero.
     * I riferimenti alle tracce embedded vanno risolti verso le istanze della libreria
     * dopo il caricamento (responsabilità del chiamante).
     *
     * @return lista di playlist ricostruite, mai {@code null}
     * @throws RuntimeException se il file esiste ma non contiene JSON valido
     */
    @Override
    public List<Playlist> load() {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            if (Files.size(file) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file.toFile(), new TypeReference<List<Playlist>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare le playlist: " + e.getMessage(), e);
        }
    }
}
