package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
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

   

    @Test
    void updateTrackChangesTitle() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.updateTrack(t, "Nuovo Titolo", t.getAuthor(), t.getDuration(), t.getGenre(), t.getYear());
        assertEquals("Nuovo Titolo", t.getTitle());
    }

    @Test
    void updateTrackChangesAuthor() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.updateTrack(t, t.getTitle(), "Nuovo Autore", t.getDuration(), t.getGenre(), t.getYear());
        assertEquals("Nuovo Autore", t.getAuthor());
    }

    @Test
    void updateTrackChangesDuration() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.updateTrack(t, t.getTitle(), t.getAuthor(), 240, t.getGenre(), t.getYear());
        assertEquals(240, t.getDuration());
    }

    @Test
    void updateTrackChangesGenre() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.updateTrack(t, t.getTitle(), t.getAuthor(), t.getDuration(), "Jazz", t.getYear());
        assertEquals("Jazz", t.getGenre());
    }

    @Test
    void updateTrackChangesYear() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.updateTrack(t, t.getTitle(), t.getAuthor(), t.getDuration(), t.getGenre(), 2010);
        assertEquals(2010, t.getYear());
    }

    @Test
    void updateTrackWithEmptyTitleThrows() {
        Track t = validTrack();
        libraryService.addTrack(t);
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.updateTrack(t, "", t.getAuthor(), t.getDuration(), t.getGenre(), t.getYear()));
    }

    @Test
    void updateTrackWithEmptyAuthorThrows() {
        Track t = validTrack();
        libraryService.addTrack(t);
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.updateTrack(t, t.getTitle(), "", t.getDuration(), t.getGenre(), t.getYear()));
    }

    @Test
    void updateTrackWithZeroDurationThrows() {
        Track t = validTrack();
        libraryService.addTrack(t);
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.updateTrack(t, t.getTitle(), t.getAuthor(), 0, t.getGenre(), t.getYear()));
    }

    @Test
    void updateTrackWithFutureYearThrows() {
        Track t = validTrack();
        libraryService.addTrack(t);
        int futureYear = Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.updateTrack(t, t.getTitle(), t.getAuthor(), t.getDuration(), t.getGenre(), futureYear));
    }

    @Test
    void updateTrackRemainsUnchangedWhenValidationFails() {
        Track t = validTrack();
        libraryService.addTrack(t);
        String originalTitle = t.getTitle();
        // titolo vuoto: la validazione fallisce prima di modificare qualsiasi campo
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.updateTrack(t, "", t.getAuthor(), t.getDuration(), t.getGenre(), t.getYear()));
        assertEquals(originalTitle, t.getTitle());
    }

    @Test
    void updateTrackReflectsInPlaylistContainingIt() {
        Track t = validTrack();
        libraryService.addTrack(t);

       
        Playlist playlist = new Playlist("Test");
        playlist.addTrack(t);

        libraryService.updateTrack(t, "Titolo Nuovo", t.getAuthor(),
            t.getDuration(), t.getGenre(), t.getYear());

        
        assertEquals("Titolo Nuovo", playlist.getTracks().get(0).getTitle());
    }

}
