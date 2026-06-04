package com.gruppo2.musicplaylistmanager.playback;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;

import java.util.List;

/**
 * Riproduzione a ciclo continuo: al termine dell'ultima traccia la riproduzione
 * ricomincia dalla prima, senza mai fermarsi.
 */
public class LoopStrategy implements PlaybackStrategy {

    /**
     * {@inheritDoc}
     * Restituisce la traccia successiva in modo circolare: dopo l'ultima
     * traccia si torna alla prima.
     *
     * @param current      playlist in riproduzione
     * @param currentTrack traccia attualmente in riproduzione
     * @return traccia successiva (mai {@code null} se la playlist non è vuota)
     */
    @Override
    public Track next(Playlist current, Track currentTrack) {
        List<Track> tracks = current.getTracks();
        if (tracks.isEmpty()) return null;
        int index = tracks.indexOf(currentTrack);
        if (index < 0) return tracks.get(0);
        return tracks.get((index + 1) % tracks.size());
    }
}
