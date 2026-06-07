package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per {@link AddTrackToLibraryCommand} e {@link RemoveTrackFromLibraryCommand}:
 * sia {@code execute()} (Sprint 1) sia {@code undo()} (US-12).
 */
class LibraryCommandTest {

    private Library        library;
    private LibraryService libraryService;
    private PlaylistService playlistService;
    private Track          track;

    @BeforeEach
    void setUp() {
        library        = new Library();
        libraryService  = new LibraryService(library);
        playlistService = new PlaylistService(library);
        track = new Track("Title", "Author", 180, "Pop", 2000);
    }

    // ===== AddTrackToLibraryCommand — execute() =====

    @Test
    void addTrackCommandExecuteAddsTrackToLibrary() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, null, track);
        cmd.execute();
        assertTrue(libraryService.getAllTracks().contains(track));
    }

    @Test
    void addTrackCommandExecuteIncreasesLibrarySize() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, null, track);
        cmd.execute();
        assertEquals(1, libraryService.getAllTracks().size());
    }

    @Test
    void addTrackCommandExecuteThrowsOnInvalidTrack() {
        Track invalid = new Track("", "Author", 180, "Pop", 2000);
        Command cmd = new AddTrackToLibraryCommand(libraryService, null, invalid);
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    void addTrackCommandExecuteFailureDoesNotModifyLibrary() {
        Track invalid = new Track("", "Author", 180, "Pop", 2000);
        assertThrows(IllegalArgumentException.class,
                () -> new AddTrackToLibraryCommand(libraryService, null, invalid).execute());
        assertTrue(libraryService.getAllTracks().isEmpty());
    }

    // ===== RemoveTrackFromLibraryCommand — execute() =====

    @Test
    void removeTrackCommandExecuteRemovesTrackFromLibrary() {
        libraryService.addTrack(track);
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, playlistService, track);
        cmd.execute();
        assertFalse(libraryService.getAllTracks().contains(track));
    }

    @Test
    void removeTrackCommandExecuteDecreasesLibrarySize() {
        libraryService.addTrack(track);
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, playlistService, track);
        cmd.execute();
        assertEquals(0, libraryService.getAllTracks().size());
    }

    @Test
    void removeTrackCommandExecuteNonPresentTrackDoesNotThrow() {
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, playlistService, track);
        assertDoesNotThrow(cmd::execute);
    }


    // ===== AddTrackToLibraryCommand — undo() (US-12) =====

    @Test
    void addTrackCommandUndo_removesTrackFromLibrary() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, null, track);
        cmd.execute();
        cmd.undo();
        assertFalse(libraryService.getAllTracks().contains(track));
    }

    @Test
    void addTrackCommandUndo_libraryIsEmptyAfterUndo() {
        Command cmd = new AddTrackToLibraryCommand(libraryService, null, track);
        cmd.execute();
        cmd.undo();
        assertTrue(libraryService.getAllTracks().isEmpty());
    }


    // ===== RemoveTrackFromLibraryCommand — undo() (US-12) =====

    @Test
    void removeTrackCommandUndo_addsTrackBackToLibrary() {
        libraryService.addTrack(track);
        Command cmd = new RemoveTrackFromLibraryCommand(libraryService, playlistService, track);
        cmd.execute();
        cmd.undo();
        assertTrue(libraryService.getAllTracks().contains(track));
    }

    @Test
    void removeTrackCommandUndo_restoresTrackToAffectedPlaylist() {
        Library lib = new Library();
        LibraryService svc = new LibraryService(lib);
        PlaylistService ps  = new PlaylistService(lib);
        svc.addTrack(track);
        Playlist playlist = new Playlist("Rock");
        playlist.addTrack(track);
        lib.addPlaylist(playlist);

        Command cmd = new RemoveTrackFromLibraryCommand(svc, ps, track);
        cmd.execute();
        assertFalse(playlist.getTracks().contains(track));

        cmd.undo();
        assertTrue(playlist.getTracks().contains(track));
        assertTrue(svc.getAllTracks().contains(track));
    }

    @Test
    void removeTrackCommandUndo_doesNotRestoreToUnaffectedPlaylist() {
        Library lib = new Library();
        LibraryService svc = new LibraryService(lib);
        PlaylistService ps  = new PlaylistService(lib);
        Track other = new Track("Other", "Artist", 120, "Pop", 2010);
        svc.addTrack(track);
        svc.addTrack(other);
        Playlist unrelated = new Playlist("Unrelated");
        unrelated.addTrack(other);
        lib.addPlaylist(unrelated);

        Command cmd = new RemoveTrackFromLibraryCommand(svc, ps, track);
        cmd.execute();
        cmd.undo();

        assertFalse(unrelated.getTracks().contains(track));
    }

}
