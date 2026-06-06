package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.PlayState;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.function.Supplier;

/**
 * Controller del pannello "Now Playing".
 * Riceve il {@link PlaybackService} condiviso e predispone il binding
 * tra le sue property osservabili e i componenti grafici della vista,
 * in modo che ogni aggiornamento si propaghi alla UI automaticamente.
 */
public class NowPlayingCtrl {

    @FXML private Label       titleLabel;
    @FXML private Label       authorLabel;
    @FXML private Label       elapsedLabel;
    @FXML private Label       durationLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Button      playPauseButton;
    @FXML private Button      skipButton;
    @FXML private Button      shuffleButton;
    @FXML private Button      loopButton;

    private PlaybackService     playbackService;
    private Supplier<Playlist>  selectedPlaylistSupplier;

    /**
     * Inietta il servizio di riproduzione condiviso e predispone i binding
     * tra le property osservabili e i componenti grafici.
     *
     * @param playbackService servizio di riproduzione condiviso
     */
    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
        // TODO (RC): implementare binding in US-10
    }

    /**
     * Inietta il supplier usato per ottenere la playlist attualmente
     * selezionata dall'utente nel pannello delle playlist.
     * Viene invocato da {@code onPlayPause()} quando la riproduzione è ferma.
     *
     * @param supplier funzione che restituisce la playlist selezionata, o {@code null}
     */
    public void setSelectedPlaylistSupplier(Supplier<Playlist> supplier) {
        this.selectedPlaylistSupplier = supplier;
    }

    /**
     * Aggiorna la vista in base allo stato corrente del service.
     */
    public void updateView() {
        // TODO (RC): implementare in US-10
    }

    /**
     * Avvia o mette in pausa la riproduzione in base allo stato corrente.
     * Se la riproduzione è ferma (STOPPED), avvia la playlist selezionata.
     * Se è in corso (PLAYING), la mette in pausa.
     * Se è in pausa (PAUSED), la riprende dal punto interrotto.
     */
    @FXML
    private void onPlayPause() {
        PlayState current = playbackService.stateProperty().get();
        if (current == PlayState.PLAYING) {
            playbackService.pause();
        } else if (current == PlayState.PAUSED) {
            playbackService.play(playbackService.getCurrentPlaylist());
        } else {
            Playlist p = selectedPlaylistSupplier != null ? selectedPlaylistSupplier.get() : null;
            if (p == null) return;
            playbackService.play(p);
        }
    }

    /**
     * Salta alla traccia successiva secondo la strategia corrente.
     */
    @FXML
    private void onSkip() {
        playbackService.skip();
    }

    /** Attiva la modalità shuffle. */
    @FXML
    private void onShuffle() {
        // TODO (FA): implementare in US-08
    }

    /** Attiva la modalità loop. */
    @FXML
    private void onLoop() {
        // TODO (FA): implementare in US-08
    }
}
