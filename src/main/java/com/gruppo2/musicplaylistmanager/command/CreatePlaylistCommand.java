package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che crea una nuova playlist nella libreria.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#createPlaylist} e lancia
 * {@link IllegalArgumentException} se il nome è vuoto, blank o duplicato.
 * {@link #undo()} è lasciato come placeholder (TODO per US-12): per annullare
 * la creazione basterà invocare {@link PlaylistService#deletePlaylist}.
 * </p>
 */
public class CreatePlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final String name;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param name            nome della nuova playlist
     */
    public CreatePlaylistCommand(PlaylistService playlistService, String name) {
        this.playlistService = playlistService;
        this.name            = name;
    }

    /**
     * Crea la playlist con il nome indicato tramite il service.
     *
     * @throws IllegalArgumentException se il nome è vuoto, blank o già esistente
     */
    @Override
    public void execute() {
        playlistService.createPlaylist(name);
    }

    /**
     * Annulla la creazione eliminando la playlist appena creata.
     * <p>
     * TODO: implementare in US-12.
     * </p>
     */
    @Override
    public void undo() {
        // TODO: implementare in US-12
    }
}
