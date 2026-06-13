package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private Library library;
    private LibraryService libraryService;

    private static Track validTrack() {
        return new Track("Title", "Author", 180, "Pop", 2000);
    }

    @BeforeEach
    void setUp() {
        library = new Library();
        libraryService = new LibraryService(library);
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

    // addTrack: duplicati

    @Test
    void addDuplicateTrackThrows() {
        libraryService.addTrack(new Track("Title", "Author", 180, "Pop", 2000));
        Track duplicate = new Track("Title", "Author", 200, "Pop", 2010);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(duplicate));
    }

    @Test
    void addDuplicateTrackCaseInsensitiveThrows() {
        libraryService.addTrack(new Track("Title", "Author", 180, "Pop", 2000));
        Track duplicate = new Track("TITLE", "author", 200, "POP", 2010);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(duplicate));
    }

    @Test
    void addTrackSameTitleAuthorDifferentGenreIsAllowed() {
        libraryService.addTrack(new Track("Title", "Author", 180, "Pop", 2000));
        Track different = new Track("Title", "Author", 180, "Rock", 2000);
        assertDoesNotThrow(() -> libraryService.addTrack(different));
        assertEquals(2, libraryService.getAllTracks().size());
    }

    @Test
    void addTrackSameTitleGenreDifferentAuthorIsAllowed() {
        libraryService.addTrack(new Track("Title", "Author", 180, "Pop", 2000));
        Track different = new Track("Title", "Other Author", 180, "Pop", 2000);
        assertDoesNotThrow(() -> libraryService.addTrack(different));
        assertEquals(2, libraryService.getAllTracks().size());
    }

    @Test
    void libraryRemainsUnchangedAfterDuplicateAdd() {
        libraryService.addTrack(new Track("Title", "Author", 180, "Pop", 2000));
        Track duplicate = new Track("Title", "Author", 200, "Pop", 2010);
        assertThrows(IllegalArgumentException.class, () -> libraryService.addTrack(duplicate));
        assertEquals(1, libraryService.getAllTracks().size());
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

    // removeTrack

    @Test
    void removeTrackDecreasesLibrarySize() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.removeTrack(t);
        assertEquals(0, libraryService.getAllTracks().size());
    }

    @Test
    void removeTrackIsNoLongerPresentInLibrary() {
        Track t = validTrack();
        libraryService.addTrack(t);
        libraryService.removeTrack(t);
        assertFalse(libraryService.getAllTracks().contains(t));
    }

    @Test
    void removeTrackLeavesOtherTracksIntact() {
        Track t1 = validTrack();
        Track t2 = new Track("B", "Y", 200, "Jazz", 2005);
        libraryService.addTrack(t1);
        libraryService.addTrack(t2);
        libraryService.removeTrack(t1);
        assertEquals(1, libraryService.getAllTracks().size());
        assertTrue(libraryService.getAllTracks().contains(t2));
    }

    @Test
    void removeTrackPropagatestoPlaylist() {
        Track t = validTrack();
        libraryService.addTrack(t);
        Playlist playlist = new Playlist("Test");
        playlist.addTrack(t);
        library.addPlaylist(playlist);
        libraryService.removeTrack(t);
        assertFalse(playlist.getTracks().contains(t));
    }

    @Test
    void removeTrackDoesNotAffectOtherTracksInPlaylist() {
        Track t1 = validTrack();
        Track t2 = new Track("B", "Y", 200, "Jazz", 2005);
        libraryService.addTrack(t1);
        libraryService.addTrack(t2);
        Playlist playlist = new Playlist("Test");
        playlist.addTrack(t1);
        playlist.addTrack(t2);
        library.addPlaylist(playlist);
        libraryService.removeTrack(t1);
        assertFalse(playlist.getTracks().contains(t1));
        assertTrue(playlist.getTracks().contains(t2));
    }

    @Test
    void removeTrackPropagatestoMultiplePlaylists() {
        Track t = validTrack();
        libraryService.addTrack(t);
        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");
        p1.addTrack(t);
        p2.addTrack(t);
        library.addPlaylist(p1);
        library.addPlaylist(p2);
        libraryService.removeTrack(t);
        assertFalse(p1.getTracks().contains(t));
        assertFalse(p2.getTracks().contains(t));
    }

    @Test
    void removeNonExistentTrackDoesNotThrow() {
        Track t = validTrack();
        assertDoesNotThrow(() -> libraryService.removeTrack(t));
    }

    // US-14a / US-14b: ricerca per genere e anno (generazione automatica di playlist)

    @Test
    void findByGenreReturnsMatchingTracks() {
        Track rock1 = new Track("A", "x", 100, "Rock", 2000);
        Track rock2 = new Track("B", "x", 100, "Rock", 2001);
        libraryService.addTrack(rock1);
        libraryService.addTrack(rock2);
        libraryService.addTrack(new Track("C", "x", 100, "Pop", 2000));
        assertEquals(2, libraryService.findByGenre("Rock").size());
        assertTrue(libraryService.findByGenre("Rock").contains(rock1));
        assertTrue(libraryService.findByGenre("Rock").contains(rock2));
    }

    @Test
    void findByGenreIsCaseInsensitive() {
        libraryService.addTrack(new Track("A", "x", 100, "Rock", 2000));
        assertEquals(1, libraryService.findByGenre("rock").size());
    }

    @Test
    void findByGenreNoMatchReturnsEmpty() {
        libraryService.addTrack(new Track("A", "x", 100, "Rock", 2000));
        assertTrue(libraryService.findByGenre("Jazz").isEmpty());
    }

    @Test
    void findByYearReturnsMatchingTracks() {
        Track y2000a = new Track("A", "x", 100, "Rock", 2000);
        Track y2000b = new Track("B", "x", 100, "Pop", 2000);
        libraryService.addTrack(y2000a);
        libraryService.addTrack(y2000b);
        libraryService.addTrack(new Track("C", "x", 100, "Jazz", 2001));
        assertEquals(2, libraryService.findByYear(2000).size());
        assertTrue(libraryService.findByYear(2000).contains(y2000a));
        assertTrue(libraryService.findByYear(2000).contains(y2000b));
    }

    @Test
    void findByYearNoMatchReturnsEmpty() {
        libraryService.addTrack(new Track("A", "x", 100, "Rock", 2000));
        assertTrue(libraryService.findByYear(1999).isEmpty());
    }

}