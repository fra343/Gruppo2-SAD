package com.gruppo2.musicplaylistmanager.playback;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Test unitari per {@link PlaybackStrategyFactory} (US-08).
 * Verifica che {@link PlaybackStrategyFactory#create(PlaybackMode)} restituisca
 * l'istanza concreta corretta per ciascun valore di {@link PlaybackMode}.
 */
class PlaybackStrategyFactoryTest {

    private final PlaybackStrategyFactory factory = new PlaybackStrategyFactory();

    @Test
    void create_sequential_returnsSequentialStrategy() {
        assertInstanceOf(SequentialStrategy.class, factory.create(PlaybackMode.SEQUENTIAL));
    }

    @Test
    void create_shuffle_returnsShuffleStrategy() {
        assertInstanceOf(ShuffleStrategy.class, factory.create(PlaybackMode.SHUFFLE));
    }

    @Test
    void create_loop_returnsLoopStrategy() {
        assertInstanceOf(LoopStrategy.class, factory.create(PlaybackMode.LOOP));
    }
}
