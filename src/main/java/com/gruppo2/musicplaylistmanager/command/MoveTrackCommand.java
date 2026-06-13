package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che sposta una traccia da una posizione a un'altra all'interno di una playlist.
 * {@link #undo()} inverte lo spostamento scambiando {@code fromIndex} e {@code toIndex}.
 */
public class MoveTrackCommand implements Command {

    private final PlaylistService playlistService;
    private final Playlist        playlist;
    private final int             fromIndex;
    private final int             toIndex;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playlist        playlist su cui operare
     * @param fromIndex       posizione corrente della traccia 
     * @param toIndex         posizione di destinazione
     */
    public MoveTrackCommand(PlaylistService playlistService,
                            Playlist playlist,
                            int fromIndex,
                            int toIndex) {
        this.playlistService = playlistService;
        this.playlist        = playlist;
        this.fromIndex       = fromIndex;
        this.toIndex         = toIndex;
    }

    @Override
    public void execute() {
        playlistService.moveTrack(playlist, fromIndex, toIndex);
    }

    @Override
    public void undo() {
        playlistService.moveTrack(playlist, toIndex, fromIndex);
    }
}
