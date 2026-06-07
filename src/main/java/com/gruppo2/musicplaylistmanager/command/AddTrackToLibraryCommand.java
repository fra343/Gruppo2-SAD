package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;

/**
 * Comando che aggiunge una traccia alla libreria.
 * <p>
 * {@link #execute()} delega a {@link LibraryService#addTrack} e lancia
 * {@link IllegalArgumentException} se i dati della traccia non superano
 * la validazione.
 * {@link #undo()} annulla l'aggiunta rimuovendo la traccia dalla libreria.
 * Prima della rimozione notifica il {@link PlaybackService}, in modo che
 * — se la traccia è in riproduzione — il player si fermi correttamente
 * anziché continuare a riprodurre una traccia non più presente nella libreria.
 * </p>
 */
public class AddTrackToLibraryCommand implements Command {

    private final LibraryService  libraryService;
    private final PlaybackService playbackService;
    private final Track           track;

    /**
     * @param libraryService  servizio condiviso per la gestione della libreria
     * @param playbackService servizio di riproduzione condiviso (può essere {@code null}
     *                        in contesti di test senza toolkit JavaFX)
     * @param track           traccia da aggiungere
     */
    public AddTrackToLibraryCommand(LibraryService libraryService,
                                    PlaybackService playbackService,
                                    Track track) {
        this.libraryService  = libraryService;
        this.playbackService = playbackService;
        this.track           = track;
    }

    /**
     * Aggiunge {@code track} alla libreria tramite il service.
     *
     * @throws IllegalArgumentException se titolo, autore o genere sono vuoti,
     *                                  la durata è &lt;= 0, o l'anno è fuori range
     */
    @Override
    public void execute() {
        libraryService.addTrack(track);
    }

    /**
     * Annulla l'aggiunta: notifica il PlaybackService (se la traccia è in
     * riproduzione viene fermata) e poi la rimuove dalla libreria e da tutte
     * le playlist che la contengono (cascade gestito da
     * {@link LibraryService#removeTrack}).
     */
    @Override
    public void undo() {
        if (playbackService != null) playbackService.notifyTrackRemoved(track);
        libraryService.removeTrack(track);
    }
}
