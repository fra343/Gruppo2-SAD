package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.PlayState;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.playback.PlaybackMode;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;

import java.util.function.Supplier;

/**
 * Controller del pannello "Now Playing".
 * Riceve il {@link PlaybackService} condiviso e predispone il binding
 * tra le sue property osservabili e i componenti grafici della vista,
 * in modo che ogni aggiornamento si propaghi alla UI automaticamente.
 */
public class NowPlayingCtrl {

    /** Pseudo-classe CSS attivata sulla riga della traccia in riproduzione (US-11). */
    private static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");

    @FXML private Label        titleLabel;
    @FXML private Label        authorLabel;
    @FXML private Label        elapsedLabel;
    @FXML private Label        durationLabel;
    @FXML private ProgressBar  progressBar;
    @FXML private Button       playPauseButton;
    @FXML private Button       skipButton;
    @FXML private ToggleButton shuffleButton;
    @FXML private ToggleButton loopButton;

    private PlaybackService    playbackService;
    private Supplier<Playlist> selectedPlaylistSupplier;

    /**
     * Inietta il servizio di riproduzione condiviso e predispone i binding
     * tra le property osservabili e i componenti grafici.
     *
     * @param playbackService servizio di riproduzione condiviso
     */
    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
        bindPlaybackProperties();
    }

    /**
     * Collega i componenti grafici del pannello alle property osservabili
     * {@code currentTrack} ed {@code elapsed} del {@link PlaybackService},
     * realizzando il pattern Observer tramite il meccanismo nativo di binding
     * di JavaFX: ogni variazione si propaga automaticamente alla UI senza
     * alcuna callback manuale (US-10).
     */
    private void bindPlaybackProperties() {
        ObjectProperty<Track> currentTrack = playbackService.currentTrackProperty();
        IntegerProperty       elapsed      = playbackService.elapsedProperty();

        // Titolo e autore della traccia corrente (placeholder se nessuna traccia)
        titleLabel.textProperty().bind(Bindings.createStringBinding(
                () -> currentTrack.get() == null
                        ? "Nessun brano in riproduzione"
                        : currentTrack.get().getTitle(),
                currentTrack));
        authorLabel.textProperty().bind(Bindings.createStringBinding(
                () -> currentTrack.get() == null ? "—" : currentTrack.get().getAuthor(),
                currentTrack));

        // Contatore del tempo trascorso e durata totale, formattati come m:ss
        elapsedLabel.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(elapsed.get()),
                elapsed));
        durationLabel.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(currentTrack.get() == null ? 0 : currentTrack.get().getDuration()),
                currentTrack));

        // Avanzamento: frazione elapsed/durata, limitata all'intervallo [0, 1]
        progressBar.progressProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    Track t = currentTrack.get();
                    if (t == null || t.getDuration() <= 0) return 0.0;
                    return Math.min(1.0, elapsed.get() / (double) t.getDuration());
                },
                elapsed, currentTrack));
    }

    /**
     * Formatta un tempo espresso in secondi nel formato {@code m:ss}
     * (minuti senza zeri iniziali, secondi sempre a due cifre).
     *
     * @param totalSeconds tempo in secondi (non negativo)
     * @return stringa nel formato minuti:secondi
     */
    private String formatTime(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
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
     * Installa sulla {@link ListView} delle tracce della playlist un rendering
     * personalizzato che evidenzia la riga della traccia attualmente in
     * riproduzione (US-11).
     * <p>
     * Ogni cella osserva la property {@code currentTrack} del
     * {@link PlaybackService}: a ogni cambio di traccia — manuale (skip) o
     * automatico (fine traccia) — l'evidenziazione si aggiorna da sé tramite il
     * binding alle property del service, senza alcuna callback esplicita.
     * L'evidenziazione attiva la pseudo-classe CSS {@code :current} sulla riga
     * corrispondente, la cui resa grafica è definita nel foglio di stile.
     *
     * @param playlistView ListView che mostra le tracce della playlist
     */
    public void highlightCurrentTrackIn(ListView<Track> playlistView) {
        playlistView.setCellFactory(view -> new ListCell<>() {

            // Listener forte (mantenuto da questo campo) registrato sulla
            // property tramite WeakInvalidationListener, così da non impedire
            // la garbage collection della cella quando non è più in uso.
            private final InvalidationListener highlightListener = obs -> updateHighlight();

            {
                playbackService.currentTrackProperty()
                        .addListener(new WeakInvalidationListener(highlightListener));
            }

            @Override
            protected void updateItem(Track item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                updateHighlight();
            }

            /**
             * Attiva la pseudo-classe {@code :current} se la traccia di questa
             * cella coincide con quella in riproduzione.
             */
            private void updateHighlight() {
                Track current = playbackService.currentTrackProperty().get();
                boolean isCurrent =
                        !isEmpty() && getItem() != null && getItem().equals(current);
                pseudoClassStateChanged(CURRENT, isCurrent);
            }
        });
    }

    /**
     * Forza un aggiornamento della vista in base allo stato corrente del service.
     * <p>
     * Con l'introduzione del binding reattivo (US-10) il pannello resta
     * costantemente sincronizzato con le property osservabili del
     * {@link PlaybackService}: ogni cambiamento di stato si riflette
     * automaticamente sulla UI, quindi un refresh esplicito non è necessario
     * in condizioni normali. Il metodo è mantenuto per coerenza con
     * l'interfaccia del controller definita nel diagramma delle classi e come
     * eventuale hook di aggiornamento manuale.
     */
    public void updateView() {
        // Nessuna azione: la sincronizzazione è garantita dai binding
        // predisposti in bindPlaybackProperties().
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

    /**
     * Attiva la modalità shuffle se il pulsante è selezionato,
     * altrimenti ripristina la modalità sequenziale.
     * Il ToggleGroup garantisce che shuffle e loop non siano attivi contemporaneamente.
     */
    @FXML
    private void onShuffle() {
        playbackService.setMode(
            shuffleButton.isSelected() ? PlaybackMode.SHUFFLE : PlaybackMode.SEQUENTIAL
        );
    }

    /**
     * Attiva la modalità loop se il pulsante è selezionato,
     * altrimenti ripristina la modalità sequenziale.
     * Il ToggleGroup garantisce che shuffle e loop non siano attivi contemporaneamente.
     */
    @FXML
    private void onLoop() {
        playbackService.setMode(
            loopButton.isSelected() ? PlaybackMode.LOOP : PlaybackMode.SEQUENTIAL
        );
    }
}
