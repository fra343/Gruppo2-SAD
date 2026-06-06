package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per il binding del pannello "Now Playing" (US-10).
 * <p>
 * Caricano {@code NowPlaying.fxml}, iniettano un {@link PlaybackService} e
 * verificano che le label, la {@link ProgressBar} e il glifo del pulsante
 * play/pausa si aggiornino automaticamente al variare delle property
 * osservabili del service, senza alcuna interazione manuale dell'utente.
 * Ogni test viene eseguito sul JavaFX Application Thread, poiché il caricamento
 * della vista e il binding richiedono il toolkit JavaFX attivo.
 */
class NowPlayingCtrlTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown(); // toolkit già inizializzato
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX toolkit non avviato");
    }

    /** Esegue {@code action} sul FX thread e rilancia eventuali eccezioni. */
    private void runFX(ThrowingRunnable action) throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try { action.run(); }
            catch (Throwable t) { error.set(t); }
            finally { latch.countDown(); }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout FX thread");
        if (error.get() != null) throw new Exception(error.get());
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    /** Carica NowPlaying.fxml e inietta il service, restituendo la radice della vista. */
    private Parent loadPanel(PlaybackService service) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gruppo2/musicplaylistmanager/view/NowPlaying.fxml"));
        Parent root = loader.load();
        NowPlayingCtrl ctrl = loader.getController();
        ctrl.setPlaybackService(service);
        return root;
    }

    private Track track(String title, String author, int duration) {
        return new Track(title, author, duration, "Rock", 2020);
    }


    //  Stato iniziale (nessun brano)

    @Test
    void noTrack_showsPlaceholderTextAndZeroProgress() throws Exception {
        runFX(() -> {
            Parent root = loadPanel(new PlaybackService());

            assertEquals("Nessun brano in riproduzione",
                    ((Label) root.lookup("#titleLabel")).getText());
            assertEquals("—", ((Label) root.lookup("#authorLabel")).getText());
            assertEquals("0:00", ((Label) root.lookup("#elapsedLabel")).getText());
            assertEquals("0:00", ((Label) root.lookup("#durationLabel")).getText());
            assertEquals(0.0, ((ProgressBar) root.lookup("#progressBar")).getProgress(), 1e-9);
        });
    }


    //  US-10 – aggiornamento di currentTrack

    @Test
    void currentTrack_updatesTitleAuthorAndDuration() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            Parent root = loadPanel(svc);

            svc.currentTrackProperty().set(track("Bohemian Rhapsody", "Queen", 354));

            assertEquals("Bohemian Rhapsody", ((Label) root.lookup("#titleLabel")).getText());
            assertEquals("Queen", ((Label) root.lookup("#authorLabel")).getText());
            assertEquals("5:54", ((Label) root.lookup("#durationLabel")).getText());
        });
    }


    //  US-10 – aggiornamento di elapsed nel tempo

    @Test
    void elapsed_updatesLabelAndProgressBar() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            Parent root = loadPanel(svc);
            svc.currentTrackProperty().set(track("Song", "Artist", 100));

            svc.elapsedProperty().set(25);

            assertEquals("0:25", ((Label) root.lookup("#elapsedLabel")).getText());
            assertEquals(0.25, ((ProgressBar) root.lookup("#progressBar")).getProgress(), 1e-9);
        });
    }

    @Test
    void elapsed_progressIsClampedToOne() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            Parent root = loadPanel(svc);
            svc.currentTrackProperty().set(track("Song", "Artist", 10));

            svc.elapsedProperty().set(15); // oltre la durata della traccia

            assertEquals(1.0, ((ProgressBar) root.lookup("#progressBar")).getProgress(), 1e-9);
        });
    }
}
