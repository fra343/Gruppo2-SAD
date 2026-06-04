package com.gruppo2.musicplaylistmanager.playback;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;

import java.util.List;

/**
 * Riproduzione sequenziale: le tracce vengono riprodotte nell'ordine della playlist.
 * Al termine dell'ultima traccia restituisce {@code null}, segnalando al
 * {@code PlaybackService} di portare lo stato a STOPPED.
 */
public class SequentialStrategy implements PlaybackStrategy {

    /**
     * {@inheritDoc}
     * Restituisce la traccia immediatamente successiva a {@code currentTrack}.
     * Se {@code currentTrack} è l'ultima della playlist, restituisce {@code null}.
     *
     * @param current      playlist in riproduzione
     * @param currentTrack traccia attualmente in riproduzione
     * @return traccia successiva, o {@code null} se non ce ne sono altre
     */
    @Override
    public Track next(Playlist current, Track currentTrack) {
        List<Track> tracks = current.getTracks();
        if (tracks.isEmpty()) return null;
        int index = tracks.indexOf(currentTrack);
        if (index < 0 || index >= tracks.size() - 1) return null;
        return tracks.get(index + 1);
    }
}
