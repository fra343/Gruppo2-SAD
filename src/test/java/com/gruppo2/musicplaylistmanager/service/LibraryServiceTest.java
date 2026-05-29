package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private LibraryService libraryService;

    private static Track validTrack() {
        return new Track("Title", "Author", 180, "Pop", 2000);
    }

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(new Library());
    }

    // addTrack: caso nominale 

    @Test
    void addValidTrackIncreasesLibrarySize() {
        libraryService.addTrack(validTrack());
        assertEquals(1, libraryService.getAllTracks().size());
    }

    @Test
    void addedTrackAppearsInGetAllTracks() {
        Track t = validTrack();
        libraryService.addTrack(t);
        assertTrue(libraryService.getAllTracks().contains(t));
    }

    @Test
    void addMultipleTracksAllPresent() {
        Track t1 = new Track("A", "X", 100, "Rock", 1990);
        Track t2 = new Track("B", "Y", 200, "Jazz", 2005);
        libraryService.addTrack(t1);
        libraryService.addTrack(t2);
        assertEquals(2, libraryService.getAllTracks().size());
        assertTrue(libraryService.getAllTracks().contains(t1));
        assertTrue(libraryService.getAllTracks().contains(t2));
    }

    // addTrack: titolo non valido

    @Test
    void addTrackWithEmptyTitleThrows() {
        Track t = new Track("", "Author", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    @Test
    void addTrackWithBlankTitleThrows() {
        Track t = new Track("   ", "Author", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    // addTrack: autore non valido 

    @Test
    void addTrackWithEmptyAuthorThrows() {
        Track t = new Track("Title", "", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    @Test
    void addTrackWithBlankAuthorThrows() {
        Track t = new Track("Title", "   ", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    // addTrack: genere non valido

    @Test
    void addTrackWithEmptyGenreThrows() {
        Track t = new Track("Title", "Author", 180, "", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    @Test
    void addTrackWithBlankGenreThrows() {
        Track t = new Track("Title", "Author", 180, "   ", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    // addTrack: durata non valida 

    @Test
    void addTrackWithZeroDurationThrows() {
        Track t = new Track("Title", "Author", 0, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    @Test
    void addTrackWithNegativeDurationThrows() {
        Track t = new Track("Title", "Author", -1, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    // addTrack: anno non valido

    @Test
    void addTrackWithYearTooOldThrows() {
        Track t = new Track("Title", "Author", 180, "Pop", 1859);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    @Test
    void addTrackWithFutureYearThrows() {
        int futureYear = Year.now().getValue() + 1;
        Track t = new Track("Title", "Author", 180, "Pop", futureYear);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
    }

    //  addTrack: la libreria rimane invariata in caso di errore

    @Test
    void libraryRemainsEmptyAfterInvalidAdd() {
        Track t = new Track("", "Author", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(t));
        assertTrue(libraryService.getAllTracks().isEmpty());
    }

    //  getAllTracks 

    @Test
    void getAllTracksReturnsEmptyListInitially() {
        assertTrue(libraryService.getAllTracks().isEmpty());
    }

    @Test
    void getAllTracksReturnsSameObservableListReference() {
        var list1 = libraryService.getAllTracks();
        var list2 = libraryService.getAllTracks();
        assertSame(list1, list2);
    }
}
