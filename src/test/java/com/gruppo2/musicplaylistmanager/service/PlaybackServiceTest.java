package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.PlayState;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.playback.PlaybackMode;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per {@link PlaybackService} (US-07a e US-07b).
 * Ogni test viene eseguito sul JavaFX Application Thread tramite
 * {@link Platform#runLater}, poiché {@link PlaybackService} utilizza
 * {@link javafx.animation.Timeline} che richiede il toolkit JavaFX attivo.
 */
class PlaybackServiceTest {

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

   
    

    private Playlist playlistWith(Track... tracks) {
        Playlist p = new Playlist("Test");
        for (Track t : tracks) p.addTrack(t);
        return p;
    }

    private Track track(String title, int duration) {
        return new Track(title, "Artista", duration, "Rock", 2020);
    }


    //  US-07a – play()               
    

    @Test
    void play_startsFromFirstTrack() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);

            PlaybackService svc = new PlaybackService();
            svc.play(p);

            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
            assertEquals(t1, svc.currentTrackProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
        });
    }

    @Test
    void play_emptyPlaylist_hasNoEffect() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            svc.play(new Playlist("Vuota"));

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
        });
    }

    @Test
    void play_differentPlaylist_restartsFromFirstTrack() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p1 = playlistWith(t1);
            Playlist p2 = playlistWith(t2);

            PlaybackService svc = new PlaybackService();
            svc.play(p1);
            svc.play(p2);

            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
            assertEquals(t2, svc.currentTrackProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
        });
    }

   
    //  US-07a – pause()                                   
    

    @Test
    void pause_whilePlaying_changeStateToPaused() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1));
            svc.pause();

            assertEquals(PlayState.PAUSED, svc.stateProperty().get());
        });
    }

    @Test
    void pause_preservesElapsed() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1));
            // Simula tempo trascorso
            svc.elapsedProperty().set(42);
            svc.pause();

            assertEquals(42, svc.elapsedProperty().get());
            assertEquals(PlayState.PAUSED, svc.stateProperty().get());
        });
    }

    @Test
    void pause_whenStopped_hasNoEffect() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            svc.pause();

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
        });
    }

    @Test
    void play_afterPause_samePlaylist_resumesWithoutResettingElapsed() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);
            svc.elapsedProperty().set(42);
            svc.pause();

            svc.play(p); // riprende

            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
            assertEquals(42, svc.elapsedProperty().get()); // elapsed preservato
        });
    }

    
    //  US-07b – skip()                                                     

    @Test
    void skip_advancesToNextTrack() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);

            PlaybackService svc = new PlaybackService();
            svc.play(p);
            svc.skip();

            assertEquals(t2, svc.currentTrackProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
        });
    }

    @Test
    void skip_onLastTrack_sequential_stopsPlayback() throws Exception {
        runFX(() -> {
            Track t1 = track("OnlyOne", 180);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1));
            svc.skip(); // ultima traccia in modalità sequenziale

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
        });
    }

    @Test
    void skip_whenStopped_hasNoEffect() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            svc.skip();

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
        });
    }

    @Test
    void skip_resetsElapsed() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);

            PlaybackService svc = new PlaybackService();
            svc.play(p);
            svc.elapsedProperty().set(90);
            svc.skip();

            assertEquals(0, svc.elapsedProperty().get());
        });
    }


    //  US-08 – setMode()

    @Test
    void setMode_doesNotInterruptCurrentTrack() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            svc.setMode(PlaybackMode.SHUFFLE);

            assertEquals(t1, svc.currentTrackProperty().get());
            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
        });
    }

    @Test
    void setMode_loop_skipWrapsAround() throws Exception {
        runFX(() -> {
            Track t1 = track("OnlyOne", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            svc.setMode(PlaybackMode.LOOP);
            svc.skip();

            assertEquals(t1, svc.currentTrackProperty().get());
            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
        });
    }

    @Test
    void setMode_sequential_afterLoop_skipsToNextThenStops() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);
            svc.setMode(PlaybackMode.LOOP);

            svc.setMode(PlaybackMode.SEQUENTIAL);
            svc.skip();

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
        });
    }


    //  US-09b – notifyTrackRemoved()

    @Test
    void notifyTrackRemoved_loopSingleTrack_stopsPlaybackImmediately() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.setMode(PlaybackMode.LOOP);
            svc.play(p);

            svc.notifyTrackRemoved(t1);

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
        });
    }

    @Test
    void notifyTrackRemoved_nonCurrentTrack_playbackContinues() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            svc.notifyTrackRemoved(t2);

            assertEquals(t1, svc.currentTrackProperty().get());
            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
        });
    }

    @Test
    void notifyTrackRemoved_currentTrack_skipsToNext() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1, t2);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            // t1 è ancora nella playlist: la strategia trova la sua posizione e restituisce t2
            svc.notifyTrackRemoved(t1);

            assertEquals(t2, svc.currentTrackProperty().get());
            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
        });
    }

    @Test
    void notifyTrackRemoved_currentTrack_noNext_stopsPlayback() throws Exception {
        runFX(() -> {
            Track t1 = track("OnlyOne", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            svc.notifyTrackRemoved(t1);

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
        });
    }

    @Test
    void notifyTrackRemoved_whenStopped_hasNoEffect() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            PlaybackService svc = new PlaybackService();

            svc.notifyTrackRemoved(t1);

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
        });
    }


    //  notifyPlaylistRemoved()

    @Test
    void notifyPlaylistRemoved_currentPlaylist_stopsPlayback() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            svc.notifyPlaylistRemoved(p);

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
            assertEquals(0, svc.elapsedProperty().get());
            assertNull(svc.getCurrentPlaylist());
        });
    }

    @Test
    void notifyPlaylistRemoved_differentPlaylist_playbackContinues() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist playing = playlistWith(t1);
            Playlist other   = playlistWith(track("Song2", 200));
            PlaybackService svc = new PlaybackService();
            svc.play(playing);

            svc.notifyPlaylistRemoved(other);

            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
            assertEquals(t1, svc.currentTrackProperty().get());
        });
    }

    @Test
    void notifyPlaylistRemoved_whenStopped_hasNoEffect() throws Exception {
        runFX(() -> {
            Playlist p = playlistWith(track("Song1", 180));
            PlaybackService svc = new PlaybackService();

            svc.notifyPlaylistRemoved(p);

            assertEquals(PlayState.STOPPED, svc.stateProperty().get());
            assertNull(svc.currentTrackProperty().get());
        });
    }


    //  US-09a – aggiunta traccia durante la riproduzione


    @Test
    void addTrackDuringPlayback_doesNotInterruptCurrentTrack() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            p.addTrack(track("Song2", 200));

            assertEquals(t1, svc.currentTrackProperty().get());
            assertEquals(PlayState.PLAYING, svc.stateProperty().get());
        });
    }

    @Test
    void addTrackDuringPlayback_newTrackReachableViaSkip() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);

            p.addTrack(t2);
            svc.skip();

            assertEquals(t2, svc.currentTrackProperty().get());
        });
    }

    @Test
    void addTrackDuringPlayback_elapsedNotReset() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Playlist p = playlistWith(t1);
            PlaybackService svc = new PlaybackService();
            svc.play(p);
            svc.elapsedProperty().set(30);

            p.addTrack(track("Song2", 200));

            assertEquals(30, svc.elapsedProperty().get());
        });
    }


    //  US-11 – getCurrentIndex()

    @Test
    void getCurrentIndex_whenStopped_returnsMinusOne() throws Exception {
        runFX(() -> {
            PlaybackService svc = new PlaybackService();
            assertEquals(-1, svc.getCurrentIndex());
        });
    }

    @Test
    void getCurrentIndex_afterPlay_returnsZero() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1, t2));

            assertEquals(0, svc.getCurrentIndex());
        });
    }

    @Test
    void getCurrentIndex_afterSkip_advancesToNextPosition() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1, t2));
            svc.skip();

            assertEquals(1, svc.getCurrentIndex());
        });
    }

    @Test
    void getCurrentIndex_loop_wrapsBackToZero() throws Exception {
        runFX(() -> {
            Track t1 = track("Song1", 180);
            Track t2 = track("Song2", 200);
            PlaybackService svc = new PlaybackService();
            svc.play(playlistWith(t1, t2));
            svc.setMode(PlaybackMode.LOOP);
            svc.skip(); // -> indice 1
            svc.skip(); // -> ritorno ciclico all'indice 0

            assertEquals(0, svc.getCurrentIndex());
        });
    }
}
