package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che aggiunge una traccia a una playlist.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#addTrackToPlaylist} e
 * lancia {@link IllegalArgumentException} se la traccia è già presente.
 * {@link #undo()} annulla l'aggiunta rimuovendo la traccia dalla playlist.
 * Prima della rimozione, se la traccia è quella in riproduzione nella stessa
 * playlist, notifica il {@link PlaybackService}: la traccia è ancora in lista
 * in quel momento, quindi la strategia può calcolare lo skip correttamente.
 * </p>
 */
public class AddTrackToPlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final PlaybackService playbackService;
    private final Playlist        playlist;
    private final Track           track;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playbackService servizio di riproduzione condiviso (può essere {@code null}
     *                        in contesti di test senza toolkit JavaFX)
     * @param playlist        playlist di destinazione
     * @param track           traccia da aggiungere
     */
    public AddTrackToPlaylistCommand(PlaylistService playlistService,
                                     PlaybackService playbackService,
                                     Playlist playlist,
                                     Track track) {
        this.playlistService = playlistService;
        this.playbackService = playbackService;
        this.playlist        = playlist;
        this.track           = track;
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
     * Annulla l'aggiunta: se la traccia è in riproduzione dalla stessa playlist
     * notifica il {@link PlaybackService} (prima della rimozione, così la strategia
     * calcola lo skip con la traccia ancora in lista), poi la rimuove dalla playlist.
     */
    @Override
    public void undo() {
        if (playbackService != null
                && playlist.equals(playbackService.getCurrentPlaylist())
                && track.equals(playbackService.currentTrackProperty().get())) {
            playbackService.notifyTrackRemoved(track);
        }
        playlistService.removeTrackFromPlaylist(playlist, track);
    }
}
