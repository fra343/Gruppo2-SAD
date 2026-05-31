package com.gruppo2.musicplaylistmanager.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Mantiene uno stack delle operazioni eseguite per supportare l'undo.
 * <p>
 * {@link #push(Command)} esegue il comando tramite {@link Command#execute()}
 * e lo conserva sullo stack delle operazioni annullabili.
 * {@link #undo()} estrae l'ultimo comando dallo stack e ne invoca
 * {@link Command#undo()}, ripristinando lo stato precedente.
 * </p>
 * Anche quando l'undo non è ancora disponibile per l'utente (US-12),
 * le operazioni passano comunque per questo meccanismo, in modo che
 * la funzionalità possa essere abilitata in un secondo momento
 * con minimo impatto sul codice esistente.
 */
public class CommandHistory {

    private final Deque<Command> history = new ArrayDeque<>();

    /**
     * Esegue il comando tramite {@link Command#execute()} e lo conserva
     * sullo stack delle operazioni annullabili.
     *
     * @param command comando da eseguire e memorizzare
     * @throws IllegalArgumentException se il comando solleva un'eccezione
     *                                  durante l'esecuzione (propagata al chiamante)
     */
    public void push(Command command) {
        command.execute();
        history.push(command);
    }

    /**
     * Annulla l'ultima operazione eseguita estraendo il comando dallo stack
     * e invocandone {@link Command#undo()}.
     * Se lo stack è vuoto il metodo non produce effetti.
     * <p>
     * TODO: collegare questo metodo all'interfaccia utente in US-12.
     * </p>
     */
    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }

    /**
     * @return {@code true} se non ci sono operazioni annullabili nello stack
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }
}
