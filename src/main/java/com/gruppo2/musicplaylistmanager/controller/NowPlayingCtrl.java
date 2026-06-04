package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Controller del pannello "Now Playing" (RC).
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

    private PlaybackService playbackService;

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
     * Aggiorna la vista in base allo stato corrente del service.
     */
    public void updateView() {
        // TODO (RC): implementare in US-10
    }

    /** Avvia o mette in pausa la riproduzione. */
    @FXML
    private void onPlayPause() {
        // TODO (LC): implementare in US-07a
    }

    /** Salta alla traccia successiva. */
    @FXML
    private void onSkip() {
        // TODO (LC): implementare in US-07b
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