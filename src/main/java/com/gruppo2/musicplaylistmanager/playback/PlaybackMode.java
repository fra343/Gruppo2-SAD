package com.gruppo2.musicplaylistmanager.playback;

/**
 * Modalità di riproduzione supportate dal sistema.
 * Usata da {@link PlaybackStrategyFactory#create(PlaybackMode)} per
 * creare la strategia corrispondente senza che i controller dipendano
 * dalle classi concrete.
 */
public enum PlaybackMode {
    SEQUENTIAL,
    SHUFFLE,
    LOOP
}
