package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

import java.util.List;

/**
 * Comando che rimuove una traccia dalla libreria (e da tutte le playlist che
 * la contengono, per effetto di {@link LibraryService#removeTrack}).
 * <p>
 * Prima di eseguire la rimozione, {@link #execute()} memorizza le playlist
 * che contengono la traccia: {@link #undo()} le usa per ripristinare lo stato
 * precedente, reinserendo la traccia nella libreria e in ciascuna playlist
 * tramite {@link PlaylistService#restoreTrackToPlaylist}, senza bypassare
 * il Service layer.
 * </p>
 */
public class RemoveTrackFromLibraryCommand implements Command {

    private final LibraryService  libraryService;
    private final PlaylistService playlistService;
    private final Track           track;
    private List<Playlist>        affectedPlaylists;

    /**
     * @param libraryService  servizio condiviso per la gestione della libreria
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param track           traccia da rimuovere
     */
    public RemoveTrackFromLibraryCommand(LibraryService libraryService,
                                         PlaylistService playlistService,
                                         Track track) {
        this.libraryService  = libraryService;
        this.playlistService = playlistService;
        this.track           = track;
    }

    /**
     * Memorizza le playlist che contengono la traccia, poi la rimuove dalla
     * libreria e, in cascade, da tutte quelle playlist.
     * Se la traccia non è presente non produce effetti.
     */
    @Override
    public void execute() {
        affectedPlaylists = libraryService.getPlaylistsContaining(track);
        libraryService.removeTrack(track);
    }

    /**
     * Annulla la rimozione: reinserisce la traccia nella libreria e in tutte
     * le playlist da cui era stata rimossa durante {@link #execute()}.
     * Usa {@link LibraryService#restoreTrack} per bypassare la validazione di
     * business (già applicata all'aggiunta originale) e
     * {@link PlaylistService#restoreTrackToPlaylist} per ripristinare le playlist
     * senza accedere direttamente al Model.
     */
    @Override
    public void undo() {
        libraryService.restoreTrack(track);
        affectedPlaylists.forEach(p -> playlistService.restoreTrackToPlaylist(p, track));
    }
}
