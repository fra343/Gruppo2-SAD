package com.gruppo2.musicplaylistmanager.playback;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;

import java.util.List;
import java.util.Random;

/**
 * Riproduzione casuale: la prossima traccia viene scelta in modo casuale tra quelle
 * della playlist, evitando di ripetere immediatamente la traccia corrente (se la
 * playlist contiene più di una traccia).
 */
public class ShuffleStrategy implements PlaybackStrategy {

    private final Random random = new Random();

    /**
     * {@inheritDoc}
     * Sceglie casualmente una traccia diversa da {@code currentTrack}.
     * Se la playlist contiene una sola traccia, restituisce la stessa traccia.
     *
     * @param current      playlist in riproduzione
     * @param currentTrack traccia attualmente in riproduzione
     * @return traccia scelta casualmente
     */
    @Override
    public Track next(Playlist current, Track currentTrack) {
        List<Track> tracks = current.getTracks();
        if (tracks.isEmpty()) return null;
        if (tracks.size() == 1) return tracks.get(0);

        Track next;
        do {
            next = tracks.get(random.nextInt(tracks.size()));
        } while (next.equals(currentTrack));
        return next;
    }
}
