package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che elimina una playlist dalla libreria.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#deletePlaylist}.
 * Se la playlist non è presente non produce effetti.
 * {@link #undo()} è lasciato come placeholder (TODO per US-12): per annullare
 * occorrerà ricreare la playlist con le stesse tracce.
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
     * Annulla l'eliminazione ricreando la playlist.
     * <p>
     * TODO: implementare in US-12.
     * </p>
     */
    @Override
    public void undo() {
        // TODO: implementare in US-12
    }
}
