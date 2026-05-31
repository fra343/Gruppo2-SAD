package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per {@link AddTrackToLibraryCommand}, {@link RemoveTrackFromLibraryCommand}.
 * I test di undo() sono omessi perché rinviati a US-12.
 */
class LibraryCommandTest {

    private LibraryService libraryService;
    private Track          track;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(new Library());
        track = new Track("Title", "Author", 180, "Pop", 2000);
    }

    // ===== AddTrackToLibraryCommand — execute() =====

    @Test
    void addTrackCommandExecuteAddsTrackToLibrary() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, track);
        cmd.execute();
        assertTrue(libraryService.getAllTracks().contains(track));
    }

    @Test
    void addTrackCommandExecuteIncreasesLibrarySize() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, track);
        cmd.execute();
        assertEquals(1, libraryService.getAllTracks().size());
    }

    @Test
    void addTrackCommandExecuteThrowsOnInvalidTrack() {
        Track invalid = new Track("", "Author", 180, "Pop", 2000);
        Command cmd = new AddTrackToLibraryCommand(libraryService, invalid);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    void addTrackCommandExecuteFailureDoesNotModifyLibrary() {
        Track invalid = new Track("", "Author", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class,
                () -> new AddTrackToLibraryCommand(libraryService, invalid).execute());
        assertTrue(libraryService.getAllTracks().isEmpty());
    }

    // ===== RemoveTrackFromLibraryCommand — execute() =====

    @Test
    void removeTrackCommandExecuteRemovesTrackFromLibrary() {
        libraryService.addTrack(track);
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, track);
        cmd.execute();
        assertFalse(libraryService.getAllTracks().contains(track));
    }

    @Test
    void removeTrackCommandExecuteDecreasesLibrarySize() {
        libraryService.addTrack(track);
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, track);
        cmd.execute();
        assertEquals(0, libraryService.getAllTracks().size());
    }

    @Test
    void removeTrackCommandExecuteNonPresentTrackDoesNotThrow() {
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, track);
        assertDoesNotThrow(cmd::execute);
    }

}
