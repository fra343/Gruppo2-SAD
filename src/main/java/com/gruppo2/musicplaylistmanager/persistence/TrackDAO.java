package com.gruppo2.musicplaylistmanager.persistence;

import com.gruppo2.musicplaylistmanager.model.Track;

import java.util.List;

/**
 * Contratto DAO per la persistenza delle tracce.
 * Le implementazioni concrete decidono il formato di storage (es. JSON, DB).
 */
public interface TrackDAO {

    /**
     * Persiste la lista di tracce nel supporto di storage.
     *
     * @param tracks lista di tracce da salvare
     */
    void save(List<Track> tracks);

    /**
     * Carica la lista di tracce dal supporto di storage.
     * Restituisce una lista vuota se il file non esiste o è vuoto.
     *
     * @return lista di tracce ricostruite
     */
    List<Track> load();
}
