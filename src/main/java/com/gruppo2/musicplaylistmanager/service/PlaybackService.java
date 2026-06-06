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
 * Servizio di riproduzione simulata 
 * Espone le property osservabili {@code currentTrack}, {@code elapsed} e {@code state}
 * a cui la UI si binda automaticamente tramite il meccanismo di JavaFX.
 * Utilizza una {@link Timeline} che avanza di un secondo alla volta e delega alla
 * {@link PlaybackStrategy} corrente la scelta della traccia successiva.
 */
public class PlaybackService {

    // --- Property osservabili 
    private final ObjectProperty<Track>     currentTrack = new SimpleObjectProperty<>();
    private final IntegerProperty           elapsed      = new SimpleIntegerProperty(0);
    private final ObjectProperty<PlayState> state        = new SimpleObjectProperty<>(PlayState.STOPPED);

    // --- Strategy pattern
    private final PlaybackStrategyFactory factory = new PlaybackStrategyFactory();
    private PlaybackStrategy strategy;
    private Playlist currentPlaylist;

    // --- Meccanismo di simulazione
    private final Timeline timeline;

    /**
     * Crea il servizio inizializzando la strategia sequenziale come default
     * e il meccanismo di simulazione basato su {@link Timeline}.
     */
    public PlaybackService() {
        strategy = factory.create(PlaybackMode.SEQUENTIAL);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> onTick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Avvia la riproduzione dalla prima traccia della playlist fornita.
     * Se la playlist è vuota non produce effetti.
     * Se la riproduzione è in pausa sulla stessa playlist, la riprende
     * dal punto in cui era stata interrotta senza azzerare {@code elapsed}.
     *
     * @param playlist playlist da riprodurre
     */
    public void play(Playlist playlist) {
        if (playlist.getTracks().isEmpty()) return;

        if (state.get() == PlayState.PAUSED && playlist.equals(currentPlaylist)) {
            state.set(PlayState.PLAYING);
            timeline.play();
            return;
        }

        timeline.stop();
        currentPlaylist = playlist;
        currentTrack.set(playlist.getTracks().get(0));
        elapsed.set(0);
        state.set(PlayState.PLAYING);
        timeline.play();
    }

    /**
     * Mette in pausa la riproduzione preservando il tempo trascorso ({@code elapsed}).
     * Se la riproduzione non è in corso non produce effetti.
     */
    public void pause() {
        if (state.get() != PlayState.PLAYING) return;
        timeline.pause();
        state.set(PlayState.PAUSED);
    }

    /**
     * Salta alla traccia successiva secondo la strategia corrente.
     * Se non esiste una traccia successiva (es. modalità sequenziale, ultima traccia),
     * porta lo stato a {@link PlayState#STOPPED}.
     */
    public void skip() {
        if (currentPlaylist == null || currentTrack.get() == null) return;
        Track next = strategy.next(currentPlaylist, currentTrack.get());
        if (next == null) {
            timeline.stop();
            state.set(PlayState.STOPPED);
            elapsed.set(0);
        } else {
            currentTrack.set(next);
            elapsed.set(0);
        }
    }

    /**
     * Sostituisce la strategia di riproduzione a runtime senza interrompere
     * la traccia corrente. La nuova strategia viene applicata alla prossima
     * transizione di traccia.
     *
     * @param mode nuova modalità di riproduzione
     */
    public void setMode(PlaybackMode mode) {
        strategy = factory.create(mode);
    }

    /**
     * Notifica il service che una traccia sta per essere rimossa dalla playlist
     * corrente. Se la traccia coincide con quella in riproduzione, forza
     * l'avanzamento alla successiva secondo la strategia attiva, o porta lo stato
     * a {@link PlayState#STOPPED} se non esiste una traccia successiva.
     * Se la traccia è diversa da quella corrente la riproduzione prosegue
     * senza interruzioni.
     * <p>
     * Deve essere invocato <em>prima</em> che la traccia venga rimossa dalla
     * playlist, in modo che la strategia possa calcolarne la posizione corretta.
     *
     * @param removed traccia che sta per essere rimossa
     */
    public void notifyTrackRemoved(Track removed) {
        if (currentPlaylist == null || currentTrack.get() == null) return;
        if (removed.equals(currentTrack.get())) {
            skip();
        }
    }

    /**
     * @return playlist attualmente in riproduzione, o {@code null} se nessuna
     */
    public Playlist getCurrentPlaylist() { return currentPlaylist; }

    /**
     * Restituisce la posizione (0-based) della traccia attualmente in
     * riproduzione all'interno della playlist corrente. Costituisce la
     * determinazione dell'indice corrente su cui si basa l'evidenziazione
     * della traccia in riproduzione nella vista della playlist (US-11).
     *
     * @return indice della traccia corrente nella playlist in riproduzione,
     *         oppure {@code -1} se nessuna traccia è in riproduzione o la
     *         traccia non è (più) presente nella playlist
     */
    public int getCurrentIndex() {
        if (currentPlaylist == null || currentTrack.get() == null) return -1;
        return currentPlaylist.getTracks().indexOf(currentTrack.get());
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
     * Chiamato dalla Timeline ogni secondo 
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
