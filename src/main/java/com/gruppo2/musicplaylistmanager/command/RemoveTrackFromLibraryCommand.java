package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;

/**
 * Comando che rimuove una traccia dalla libreria (e da tutte le playlist che
 * la contengono, per effetto di {@link LibraryService#removeTrack}).
 * <p>
 * {@link #undo()} è lasciato come placeholder (TODO per US-12): per annullare
 * la rimozione occorrerà riaggiungerla alla libreria e alle playlist originarie.
 * </p>
 */
public class RemoveTrackFromLibraryCommand implements Command {

    private final LibraryService libraryService;
    private final Track track;

    /**
     * @param libraryService servizio condiviso per la gestione della libreria
     * @param track          traccia da rimuovere
     */
    public RemoveTrackFromLibraryCommand(LibraryService libraryService, Track track) {
        this.libraryService = libraryService;
        this.track          = track;
    }

    /**
     * Rimuove {@code track} dalla libreria e da tutte le playlist che la contengono.
     * Se la traccia non è presente non produce effetti.
     */
    @Override
    public void execute() {
        libraryService.removeTrack(track);
    }

    /**
     * Annulla la rimozione riaggungendo la traccia alla libreria.
     * <p>
     * TODO: implementare in US-12.
     * </p>
     */
    @Override
    public void undo() {
        // TODO: implementare in US-12
    }
}
