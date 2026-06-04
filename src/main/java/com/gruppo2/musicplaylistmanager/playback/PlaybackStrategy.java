package com.gruppo2.musicplaylistmanager.playback;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;

/**
 * Strategia di riproduzione .
 * Incapsula l'algoritmo che determina quale traccia riprodurre dopo quella corrente,
 * permettendo di cambiare modalità a runtime senza modificare il {@code PlaybackService}.
 */
public interface PlaybackStrategy {

    /**
     * Restituisce la prossima traccia da riprodurre.
     *
     * @param current      playlist in riproduzione
     * @param currentTrack traccia attualmente in riproduzione
     * @return la prossima traccia, o {@code null} se la riproduzione deve fermarsi
     */
    Track next(Playlist current, Track currentTrack);
}
