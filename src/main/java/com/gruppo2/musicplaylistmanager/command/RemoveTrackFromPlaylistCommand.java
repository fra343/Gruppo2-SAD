package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che rimuove una traccia da una playlist.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#removeTrackFromPlaylist}.
 * {@link #undo()} annulla la rimozione reinserendo la traccia nella playlist.
 * </p>
 */
public class RemoveTrackFromPlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final Playlist playlist;
    private final Track track;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playlist        playlist da cui rimuovere
     * @param track           traccia da rimuovere
     */
    public RemoveTrackFromPlaylistCommand(PlaylistService playlistService,
                                          Playlist playlist,
                                          Track track) {
        this.playlistService = playlistService;
        this.playlist         = playlist;
        this.track            = track;
    }

    /** Rimuove {@code track} da {@code playlist} tramite il service. */
    @Override
    public void execute() {
        playlistService.removeTrackFromPlaylist(playlist, track);
    }

    /** Annulla la rimozione reinserendo la traccia nella playlist. */
    @Override
    public void undo() {
        playlistService.restoreTrackToPlaylist(playlist, track);
    }
}
