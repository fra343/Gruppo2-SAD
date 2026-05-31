package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che aggiunge una traccia a una playlist.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#addTrackToPlaylist} e
 * lancia {@link IllegalArgumentException} se la traccia è già presente.
 * {@link #undo()} è lasciato come placeholder non implementato (TODO per US-12):
 * per annullare l'aggiunta basterà invocare
 * {@link PlaylistService#removeTrackFromPlaylist}.
 * </p>
 */
public class AddTrackToPlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final Playlist playlist;
    private final Track track;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playlist        playlist di destinazione
     * @param track           traccia da aggiungere
     */
    public AddTrackToPlaylistCommand(PlaylistService playlistService,
                                     Playlist playlist,
                                     Track track) {
        this.playlistService = playlistService;
        this.playlist         = playlist;
        this.track            = track;
    }

    /**
     * Aggiunge {@code track} a {@code playlist} tramite il service.
     *
     * @throws IllegalArgumentException se la traccia è già presente nella playlist
     */
    @Override
    public void execute() {
        playlistService.addTrackToPlaylist(playlist, track);
    }

    /**
     * Annulla l'aggiunta rimuovendo la traccia dalla playlist.
     * <p>
     * TODO: implementare in US-12.
     * </p>
     */
    @Override
    public void undo() {
        // TODO: implementare in US-12
    }
}
