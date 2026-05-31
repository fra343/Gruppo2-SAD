package com.gruppo2.musicplaylistmanager.command;

/**
 * Interfaccia del pattern Command.
 * Ogni operazione modificante sul Model viene incapsulata in un oggetto Command
 * che espone {@link #execute()} per applicarla e {@link #undo()} per annullarla.
 * <p>
 * Pensare in termini di Command fin dall'inizio rende l'aggiunta dell'undo
 * un'operazione non invasiva: i controller e i service non cambiano,
 * si implementa soltanto il corpo di {@code undo()} nelle classi concrete.
 * </p>
 * Il metodo {@code undo()} è previsto dall'architettura ma la sua implementazione
 * concreta è rinviata a US-12.
 */
public interface Command {

    /** Esegue l'operazione incapsulata nel comando. */
    void execute();

    /**
     * Annulla l'operazione eseguita da {@link #execute()},
     * riportando il Model allo stato precedente.
     * <p>
     * TODO: implementare nelle classi concrete in US-12.
     * </p>
     */
    void undo();
}
