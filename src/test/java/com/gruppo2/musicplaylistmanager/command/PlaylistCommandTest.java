package com.gruppo2.musicplaylistmanager.command;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per {@link AddTrackToPlaylistCommand}, {@link RemoveTrackFromPlaylistCommand}
 * e {@link CommandHistory#push}.
 * I test di undo() sono omessi perché rinviati a US-12.
 */
class PlaylistCommandTest {

    private PlaylistService playlistService;
    private Playlist        playlist;
    private Track           track;

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService(new Library());
        playlistService.createPlaylist("Test");
        playlist = playlistService.getAllPlaylists().get(0);
        track    = new Track("Title", "Author", 180, "Pop", 2000);
    }

    // ===== AddTrackToPlaylistCommand — execute() =====

    @Test
    void addCommandExecuteAddsTrackToPlaylist() {
        Command cmd = new AddTrackToPlaylistCommand(playlistService, playlist, track);
        cmd.execute();
        assertTrue(playlist.getTracks().contains(track));
    }

    @Test
    void addCommandExecuteIncreasesPlaylistSize() {
        Command cmd = new AddTrackToPlaylistCommand(playlistService, playlist, track);
        cmd.execute();
        assertEquals(1, playlist.getTracks().size());
    }

    @Test
    void addCommandExecuteThrowsOnDuplicate() {
        Command cmd = new AddTrackToPlaylistCommand(playlistService, playlist, track);
        cmd.execute();
        Command duplicate = new AddTrackToPlaylistCommand(playlistService, playlist, track);
        assertThrows(IllegalArgumentException.class, duplicate::execute);
    }

    @Test
    void addCommandExecuteDuplicateDoesNotModifyPlaylist() {
        new AddTrackToPlaylistCommand(playlistService, playlist, track).execute();
        assertThrows(IllegalArgumentException.class,
                () -> new AddTrackToPlaylistCommand(playlistService, playlist, track).execute());
        assertEquals(1, playlist.getTracks().size());
    }

    // ===== RemoveTrackFromPlaylistCommand — execute() =====

    @Test
    void removeCommandExecuteRemovesTrackFromPlaylist() {
        playlistService.addTrackToPlaylist(playlist, track);
        Command cmd = new RemoveTrackFromPlaylistCommand(playlistService, playlist, track);
        cmd.execute();
        assertFalse(playlist.getTracks().contains(track));
    }

    @Test
    void removeCommandExecuteDecreasesPlaylistSize() {
        playlistService.addTrackToPlaylist(playlist, track);
        Command cmd = new RemoveTrackFromPlaylistCommand(playlistService, playlist, track);
        cmd.execute();
        assertEquals(0, playlist.getTracks().size());
    }

    @Test
    void removeCommandExecuteNonPresentTrackDoesNotThrow() {
        Command cmd = new RemoveTrackFromPlaylistCommand(playlistService, playlist, track);
        assertDoesNotThrow(cmd::execute);
    }

    // ===== CommandHistory — push() =====

    @Test
    void commandHistoryPushExecutesCommand() {
        CommandHistory history = new CommandHistory();
        history.push(new AddTrackToPlaylistCommand(playlistService, playlist, track));
        assertTrue(playlist.getTracks().contains(track));
    }

    @Test
    void commandHistoryIsNotEmptyAfterPush() {
        CommandHistory history = new CommandHistory();
        assertTrue(history.isEmpty());
        history.push(new AddTrackToPlaylistCommand(playlistService, playlist, track));
        assertFalse(history.isEmpty());
    }

    @Test
    void commandHistoryPushPropagatesExceptionWithoutStoringCommand() {
        CommandHistory history = new CommandHistory();
        playlistService.addTrackToPlaylist(playlist, track);
        assertThrows(IllegalArgumentException.class,
                () -> history.push(new AddTrackToPlaylistCommand(playlistService, playlist, track)));
        assertTrue(history.isEmpty(), "Il comando fallito non deve essere nello stack");
    }

    // ===== CreatePlaylistCommand — execute() =====

    @Test
    void createPlaylistCommandExecuteAddsPlaylist() {
        Command cmd = new CreatePlaylistCommand(playlistService, "Pop");
        cmd.execute();
        assertEquals(2, playlistService.getAllPlaylists().size()); // "Test" + "Pop"
    }

    @Test
    void createPlaylistCommandExecutePlaylistAppearsInList() {
        Command cmd = new CreatePlaylistCommand(playlistService, "Pop");
        cmd.execute();
        assertTrue(playlistService.getAllPlaylists().stream()
                .anyMatch(p -> p.getName().equals("Pop")));
    }

    @Test
    void createPlaylistCommandExecuteThrowsOnDuplicateName() {
        Command cmd = new CreatePlaylistCommand(playlistService, "Test"); // "Test" già presente
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    @Test
    void createPlaylistCommandExecuteThrowsOnEmptyName() {
        Command cmd = new CreatePlaylistCommand(playlistService, "");
        assertThrows(IllegalArgumentException.class, cmd::execute);
    }

    // ===== DeletePlaylistCommand — execute() =====

    @Test
    void deletePlaylistCommandExecuteRemovesPlaylist() {
        Command cmd = new DeletePlaylistCommand(playlistService, playlist);
        cmd.execute();
        assertFalse(playlistService.getAllPlaylists().contains(playlist));
    }

    @Test
    void deletePlaylistCommandExecuteDecreasesSize() {
        Command cmd = new DeletePlaylistCommand(playlistService, playlist);
        cmd.execute();
        assertEquals(0, playlistService.getAllPlaylists().size());
    }
}
