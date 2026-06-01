package com.gruppo2.musicplaylistmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    private static final String TITLE  = "Bohemian Rhapsody";
    private static final String AUTHOR = "Queen";
    private static final int    DURATION = 354;
    private static final String GENRE  = "Rock";
    private static final int    YEAR   = 1975;

    private Track track;

    @BeforeEach
    void setUp() {
        track = new Track(TITLE, AUTHOR, DURATION, GENRE, YEAR);
    }

    // Costruttore e getters 

    @Test
    void constructorSetsTitle() {
        assertEquals(TITLE, track.getTitle());
    }

    @Test
    void constructorSetsAuthor() {
        assertEquals(AUTHOR, track.getAuthor());
    }

    @Test
    void constructorSetsDuration() {
        assertEquals(DURATION, track.getDuration());
    }

    @Test
    void constructorSetsGenre() {
        assertEquals(GENRE, track.getGenre());
    }

    @Test
    void constructorSetsYear() {
        assertEquals(YEAR, track.getYear());
    }

    @Test
    void getIdReturnsNonNull() {
        assertNotNull(track.getId());
    }

    @Test
    void twoDistinctTracksHaveDifferentIds() {
        Track other = new Track(TITLE, AUTHOR, DURATION, GENRE, YEAR);
        assertNotEquals(track.getId(), other.getId());
    }

    @Test
    void getTagsReturnsEmptySetInitially() {
        assertTrue(track.getTags().isEmpty());
    }

    // equals 

    @Test
    void equalsWithSameInstanceReturnsTrue() {
        assertEquals(track, track);
    }

    @Test
    void equalsWithDifferentTrackReturnsFalse() {
        Track other = new Track(TITLE, AUTHOR, DURATION, GENRE, YEAR);
        assertNotEquals(track, other);
    }

    @Test
    void equalsWithNullReturnsFalse() {
        assertNotEquals(null, track);
    }

    @Test
    void equalsWithDifferentTypeReturnsFalse() {
        assertNotEquals("not a track", track);
    }

    // hashCode 

    @Test
    void hashCodeIsConsistent() {
        assertEquals(track.hashCode(), track.hashCode());
    }

    //  toString 

    @Test
    void toStringContainsTitleAuthorYear() {
        String s = track.toString();
        assertTrue(s.contains(TITLE));
        assertTrue(s.contains(AUTHOR));
        assertTrue(s.contains(String.valueOf(YEAR)));
    }
}
