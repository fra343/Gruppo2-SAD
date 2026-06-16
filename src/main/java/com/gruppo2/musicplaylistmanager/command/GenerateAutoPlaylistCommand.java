package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

import java.util.List;

/**
 * Comando che genera una playlist automatica a partire da una lista di tracce
 * (per genere, anno o tag).
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#generateAutoPlaylist} e
 * lancia {@link IllegalArgumentException} se la lista è vuota o il nome è già usato.
 * Cattura il riferimento alla {@link Playlist} creata, necessario a {@link #undo()}.
 * {@link #undo()} elimina la playlist generata; se era in riproduzione notifica il
 * {@link PlaybackService} prima della rimozione.
 * </p>
 */
public class GenerateAutoPlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final PlaybackService playbackService;
    private final String          name;
    private final List<Track>     tracks;
    private Playlist              createdPlaylist;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param playbackService servizio di riproduzione condiviso (può essere {@code null}
     *                        in contesti di test senza toolkit JavaFX)
     * @param name            nome della playlist da generare
     * @param tracks          tracce selezionate dal criterio di ricerca
     */
    public GenerateAutoPlaylistCommand(PlaylistService playlistService,
                                       PlaybackService playbackService,
                                       String name,
                                       List<Track> tracks) {
        this.playlistService = playlistService;
        this.playbackService = playbackService;
        this.name            = name;
        this.tracks          = tracks;
    }

    /**
     * Genera la playlist con il nome e le tracce indicati tramite il service e
     * ne cattura il riferimento restituito, necessario per un eventuale undo successivo.
     *
     * @throws IllegalArgumentException se la lista di tracce è vuota
     *                                  o esiste già una playlist con lo stesso nome
     */
    @Override
    public void execute() {
        createdPlaylist = playlistService.generateAutoPlaylist(name, tracks);
    }

    /**
     * Annulla la generazione eliminando la playlist creata.
     * Se la playlist era in riproduzione, notifica il {@link PlaybackService}
     * prima della rimozione così che lo stato di riproduzione venga azzerato.
     */
    @Override
    public void undo() {
        if (createdPlaylist == null) return;
        if (playbackService != null
                && createdPlaylist.equals(playbackService.getCurrentPlaylist())) {
            playbackService.notifyPlaylistRemoved(createdPlaylist);
        }
        playlistService.deletePlaylist(createdPlaylist);
    }
}
