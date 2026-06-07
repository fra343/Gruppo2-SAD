package com.gruppo2.musicplaylistmanager.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

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

    private final Deque<Command>  history  = new ArrayDeque<>();
    private final BooleanProperty canUndo  = new SimpleBooleanProperty(false);

    /**
     * Esegue il comando tramite {@link Command#execute()} e lo conserva
     * sullo stack delle operazioni annullabili.
     * Aggiorna {@link #canUndoProperty()} a {@code true} dopo la push.
     *
     * @param command comando da eseguire e memorizzare
     * @throws IllegalArgumentException se il comando solleva un'eccezione
     *                                  durante l'esecuzione (propagata al chiamante,
     *                                  il comando non viene memorizzato)
     */
    public void push(Command command) {
        command.execute();
        history.push(command);
        canUndo.set(true);
    }

    /**
     * Annulla l'ultima operazione eseguita estraendo il comando dallo stack
     * e invocandone {@link Command#undo()}.
     * Aggiorna {@link #canUndoProperty()} in base allo stato risultante dello stack.
     * Se lo stack è vuoto il metodo non produce effetti.
     */
    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
            canUndo.set(!history.isEmpty());
        }
    }

    /**
     * Property osservabile che indica se è disponibile almeno un'operazione
     * annullabile. Il pulsante "Annulla" nella UI si lega a questa property
     * per abilitarsi/disabilitarsi automaticamente (US-12).
     *
     * @return property booleana {@code true} se lo stack non è vuoto
     */
    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    /**
     * @return {@code true} se non ci sono operazioni annullabili nello stack
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }
}
