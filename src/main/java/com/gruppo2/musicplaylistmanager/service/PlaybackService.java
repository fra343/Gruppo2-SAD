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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servizio di riproduzione simulata 
 * Espone le property osservabili {@code currentTrack}, {@code elapsed} e {@code state}
 * a cui la UI si binda automaticamente tramite il meccanismo di JavaFX.
 * Utilizza una {@link Timeline} che avanza di un secondo alla volta e delega alla
 * {@link PlaybackStrategy} corrente la scelta della traccia successiva.
 */
public class PlaybackService {

    // --- Property osservabili
    private final ObjectProperty<Track>        currentTrack = new SimpleObjectProperty<>();
    private final IntegerProperty              elapsed      = new SimpleIntegerProperty(0);
    private final ObjectProperty<PlayState>    state        = new SimpleObjectProperty<>(PlayState.STOPPED);
    private final ObjectProperty<PlaybackMode> mode         = new SimpleObjectProperty<>(PlaybackMode.SEQUENTIAL);

    // --- Strategy pattern
    private final PlaybackStrategyFactory factory = new PlaybackStrategyFactory();
    private PlaybackStrategy strategy;
    private Playlist currentPlaylist;

    // --- Conteggio riproduzioni
    private final Map<UUID, Integer> trackPlayCounts    = new HashMap<>();
    private final Map<UUID, Integer> playlistPlayCounts = new HashMap<>();

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
        incrementPlaylistCount(playlist);
        Track first = playlist.getTracks().get(0);
        incrementTrackCount(first);
        currentTrack.set(first);
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
            currentTrack.set(null);
            state.set(PlayState.STOPPED);
            elapsed.set(0);
        } else {
            incrementTrackCount(next);
            currentTrack.set(next);
            elapsed.set(0);
        }
    }

    /**
     * Avvia la riproduzione di una singola traccia.
     * Crea internamente una playlist temporanea di un solo elemento e la
     * passa a {@link #play(Playlist)}: in modalità sequenziale la riproduzione
     * si ferma al termine della traccia; in modalità loop la ripete.
     *
     * @param track traccia da riprodurre
     */
    public void play(Track track) {
        Playlist single = new Playlist(track.getTitle());
        single.addTrack(track);
        play(single);
    }

    /**
     * Avvia la riproduzione di una playlist a partire da una traccia specifica.
     * Utile quando l'utente vuole iniziare dalla traccia N e proseguire
     * con le successive secondo la strategia corrente.
     *
     * @param playlist  playlist da riprodurre
     * @param startFrom traccia da cui iniziare (deve essere presente nella playlist)
     */
    public void play(Playlist playlist, Track startFrom) {
        if (!playlist.getTracks().contains(startFrom)) return;
        timeline.stop();
        currentPlaylist = playlist;
        incrementPlaylistCount(playlist);
        incrementTrackCount(startFrom);
        currentTrack.set(startFrom);
        elapsed.set(0);
        state.set(PlayState.PLAYING);
        timeline.play();
    }

    /**
     * Sostituisce la strategia di riproduzione a runtime senza interrompere
     * la traccia corrente. La nuova strategia viene applicata alla prossima
     * transizione di traccia.
     *
     * @param mode nuova modalità di riproduzione
     */
    public void setMode(PlaybackMode mode) {
        this.mode.set(mode);
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
     * <p>
     * Caso limite — modalità loop con playlist da un solo elemento: {@code skip()}
     * restituisce la stessa traccia (wrap-around), che sta per essere eliminata.
     * In questo caso viene forzato lo stop per evitare che la riproduzione continui
     * con una traccia non più presente nella playlist.
     *
     * @param removed traccia che sta per essere rimossa
     */
    public void notifyTrackRemoved(Track removed) {
        if (currentPlaylist == null || currentTrack.get() == null) return;
        if (removed.equals(currentTrack.get())) {
            skip();
            // Se skip() non ha avanzato (es. loop su playlist da 1 traccia),
            // currentTrack punta ancora alla traccia rimossa: forza lo stop.
            if (removed.equals(currentTrack.get())) {
                timeline.stop();
                currentTrack.set(null);
                state.set(PlayState.STOPPED);
                elapsed.set(0);
            }
        }
    }

    /**
     * Notifica il service che una playlist sta per essere eliminata dalla libreria.
     * Se la playlist coincide con quella in riproduzione, ferma immediatamente
     * la riproduzione e azzera lo stato.
     * Se la playlist è diversa da quella corrente non produce effetti.
     *
     * @param removed playlist che sta per essere eliminata
     */
    public void notifyPlaylistRemoved(Playlist removed) {
        if (removed.equals(currentPlaylist)) {
            timeline.stop();
            currentPlaylist = null;
            currentTrack.set(null);
            state.set(PlayState.STOPPED);
            elapsed.set(0);
        }
    }

    /**
     * @return playlist attualmente in riproduzione, o {@code null} se nessuna
     */
    public Playlist getCurrentPlaylist() { return currentPlaylist; }

    /**
     * Restituisce la posizione (0-based) della traccia attualmente in
     * riproduzione all'interno della playlist corrente. Usato per evidenziare
     * la traccia in riproduzione nella vista della playlist.
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
     * @return property osservabile della modalità di riproduzione corrente
     */
    public ObjectProperty<PlaybackMode> modeProperty() { return mode; }

    /**
     * Restituisce il numero di volte che la traccia è stata riprodotta nella sessione corrente.
     *
     * @param track traccia di cui leggere il conteggio
     * @return numero di riproduzioni, 0 se la traccia non è mai stata riprodotta
     */
    public int getTrackPlayCount(Track track) {
        return trackPlayCounts.getOrDefault(track.getId(), 0);
    }

    /**
     * Restituisce il numero di volte che la playlist è stata avviata nella sessione corrente.
     *
     * @param playlist playlist di cui leggere il conteggio
     * @return numero di avvii, 0 se la playlist non è mai stata avviata
     */
    public int getPlaylistPlayCount(Playlist playlist) {
        return playlistPlayCounts.getOrDefault(playlist.getId(), 0);
    }

    private void incrementTrackCount(Track track) {
        if (track != null)
            trackPlayCounts.merge(track.getId(), 1, Integer::sum);
    }

    private void incrementPlaylistCount(Playlist playlist) {
        if (playlist != null)
            playlistPlayCounts.merge(playlist.getId(), 1, Integer::sum);
    }

    /**
     * Chiamato dalla Timeline ogni secondo.
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
                currentTrack.set(null);
                state.set(PlayState.STOPPED);
                elapsed.set(0);
            } else {
                incrementTrackCount(next);
                currentTrack.set(next);
                elapsed.set(0);
            }
        }
    }
}
