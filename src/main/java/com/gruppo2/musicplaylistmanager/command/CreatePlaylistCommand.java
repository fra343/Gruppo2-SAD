package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;

/**
 * Comando che crea una nuova playlist nella libreria.
 * <p>
 * {@link #execute()} delega a {@link PlaylistService#createPlaylist} e lancia
 * {@link IllegalArgumentException} se il nome è vuoto, blank o duplicato.
 * Dopo la creazione cattura il riferimento all'oggetto {@link Playlist} appena
 * aggiunto, necessario a {@link #undo()} per eliminare esattamente quella istanza.
 * </p>
 */
public class CreatePlaylistCommand implements Command {

    private final PlaylistService playlistService;
    private final String          name;
    private Playlist              createdPlaylist;

    /**
     * @param playlistService servizio condiviso per la gestione delle playlist
     * @param name            nome della nuova playlist
     */
    public CreatePlaylistCommand(PlaylistService playlistService, String name) {
        this.playlistService = playlistService;
        this.name            = name;
    }

    /**
     * Crea la playlist con il nome indicato tramite il service e ne cattura
     * il riferimento restituito, necessario per un eventuale undo successivo.
     *
     * @throws IllegalArgumentException se il nome è vuoto, blank o già esistente
     */
    @Override
    public void execute() {
        createdPlaylist = playlistService.createPlaylist(name);
    }

    /**
     * Annulla la creazione eliminando la playlist appena creata.
     */
    @Override
    public void undo() {
        if (createdPlaylist != null) {
            playlistService.deletePlaylist(createdPlaylist);
        }
    }
}
