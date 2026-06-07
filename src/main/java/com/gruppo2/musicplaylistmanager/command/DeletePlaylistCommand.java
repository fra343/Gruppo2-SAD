package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che elimina una playlist dalla libreria.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#deletePlaylist}.
 * Se la playlist non è presente non produce effetti.
 * {@link #undo()} ripristina l'oggetto {@link Playlist} originale
 * (con UUID e tracce invariati) tramite {@link PlaylistService#restorePlaylist}.
 * </p>
 */
public class DeletePlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final Playlist playlist;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playlist        playlist da eliminare
     */
    public DeletePlaylistCommand(PlaylistService playlistService, Playlist playlist) {
        this.playlistService = playlistService;
        this.playlist        = playlist;
    }

    /**
     * Elimina {@code playlist} dalla libreria tramite il service.
     */
    @Override
    public void execute() {
        playlistService.deletePlaylist(playlist);
    }

    /**
     * Annulla l'eliminazione ripristinando l'oggetto {@link Playlist} originale
     * (con UUID e tracce invariati) tramite {@link PlaylistService#restorePlaylist}.
     */
    @Override
    public void undo() {
        playlistService.restorePlaylist(playlist);
    }
}
