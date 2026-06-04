package com.gruppo2.musicplaylistmanager.playback;

/**
 * Factory per la creazione delle strategie di riproduzione (pattern Factory).
 * Centralizza il punto di estensione: aggiungere una nuova modalità richiede
 * un nuovo valore in {@link PlaybackMode}, una nuova classe Strategy e un
 * solo ramo nel metodo {@link #create(PlaybackMode)}, senza modificare i siti
 * di chiamata.
 */
public class PlaybackStrategyFactory {

    /**
     * Crea e restituisce la strategia corrispondente alla modalità richiesta.
     *
     * @param mode modalità di riproduzione desiderata
     * @return istanza di {@link PlaybackStrategy} corrispondente
     */
    public PlaybackStrategy create(PlaybackMode mode) {
        return switch (mode) {
            case SEQUENTIAL -> new SequentialStrategy();
            case SHUFFLE    -> new ShuffleStrategy();
            case LOOP       -> new LoopStrategy();
        };
    }
}
