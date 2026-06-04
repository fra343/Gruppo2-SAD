package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.PlayState;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.playback.PlaybackMode;
import com.gruppo2.musicplaylistmanager.playback.PlaybackStrategy;
import com.gruppo2.musicplaylistmanager.playback.PlaybackStrategyFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;

/**
 * Espone le property osservabili {@code currentTrack}, {@code elapsed} e {@code state}
 * a cui la UI si binda automaticamente tramite il meccanismo di JavaFX.
 * Utilizza una {@link Timeline} che avanza di un secondo alla volta e delega alla
 * {@link PlaybackStrategy} corrente la scelta della traccia successiva.
 */
public class PlaybackService {

    
    private final ObjectProperty<Track>    currentTrack = new SimpleObjectProperty<>();
    private final IntegerProperty          elapsed      = new SimpleIntegerProperty(0);
    private final ObjectProperty<PlayState> state       = new SimpleObjectProperty<>(PlayState.STOPPED);

   
    private final PlaybackStrategyFactory factory = new PlaybackStrategyFactory();
    private PlaybackStrategy strategy;
    private Playlist currentPlaylist;

   
    private final Timeline timeline;

    /**
     * Crea il servizio inizializzando la strategia sequenziale come default
     * e il meccanismo di simulazione basato su {@link Timeline}.
     */
    public PlaybackService() {
        strategy = factory.create(PlaybackMode.SEQUENTIAL);

        // Setup Timeline: tick ogni secondo 
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> onTick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Avvia la riproduzione dalla prima traccia della playlist fornita.
     * Se la playlist è vuota non produce effetti.
     *
     * @param playlist playlist da riprodurre
     */
    public void play(Playlist playlist) {
        // TODO (LC): implementare in US-07a
    }

    /**
     * Mette in pausa la riproduzione preservando il tempo trascorso ({@code elapsed}).
     * Se la riproduzione non è in corso non produce effetti.
     */
    public void pause() {
        // TODO (LC): implementare in US-07a
    }

    /**
     * Salta alla traccia successiva secondo la strategia corrente.
     * Se non esiste una traccia successiva (es. modalità sequenziale, ultima traccia),
     * porta lo stato a {@link PlayState#STOPPED}.
     */
    public void skip() {
        // TODO (LC): implementare in US-07b
    }

    /**
     * Sostituisce la strategia di riproduzione a runtime senza interrompere
     * la traccia corrente. La nuova strategia viene applicata alla prossima
     * transizione di traccia.
     *
     * @param mode nuova modalità di riproduzione
     */
    public void setMode(PlaybackMode mode) {
        // TODO (FA): implementare in US-08
    }

    /**
     * @return property osservabile della traccia attualmente in riproduzione
     */
    public ObjectProperty<Track> currentTrackProperty() { return currentTrack; }

    /**
     * @return property osservabile del tempo trascorso in secondi
     */
    public IntegerProperty elapsedProperty() { return elapsed; }

    /**
     * @return property osservabile dello stato di riproduzione
     */
    public ObjectProperty<PlayState> stateProperty() { return state; }

    /**
     * Chiamato dalla Timeline ogni secondo (LC).
     * Incrementa {@code elapsed} e, quando la traccia corrente è terminata,
     * interroga la strategia per determinare la prossima traccia o ferma la riproduzione.
     */
    private void onTick() {
        if (currentPlaylist == null || currentTrack.get() == null) return;

        elapsed.set(elapsed.get() + 1);
        Track current = currentTrack.get();

        if (elapsed.get() >= current.getDuration()) {
            Track next = strategy.next(currentPlaylist, current);
            if (next == null) {
                timeline.stop();
                state.set(PlayState.STOPPED);
                elapsed.set(0);
            } else {
                currentTrack.set(next);
                elapsed.set(0);
            }
        }
    }
}