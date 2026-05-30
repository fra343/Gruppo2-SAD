package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


//Test per PlaylistService
class PlaylistServiceTest {

    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService();
    }

    // CreatePlaylist 

    @Test
    void createPlaylistIncreasesSize() {
        playlistService.createPlaylist("Rock classico");
        assertEquals(1, playlistService.getAllPlaylists().size());
    }

    @Test
    void createdPlaylistAppearsInGetAll() {
        playlistService.createPlaylist("Jazz");
        assertEquals("Jazz", playlistService.getAllPlaylists().get(0).getName());
    }

    @Test
    void createMultiplePlaylistsAllPresent() {
        playlistService.createPlaylist("Rock");
        playlistService.createPlaylist("Pop");
        playlistService.createPlaylist("Jazz");
        assertEquals(3, playlistService.getAllPlaylists().size());
    }

    @Test
    void createPlaylistWithEmptyNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.createPlaylist(""));
    }

    @Test
    void createPlaylistWithBlankNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.createPlaylist("   "));
    }

    @Test
    void createPlaylistWithDuplicateNameThrows() {
        playlistService.createPlaylist("Rock");
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.createPlaylist("Rock"));
    }

    @Test
    void createPlaylistDuplicateCheckIsCaseInsensitive() {
        playlistService.createPlaylist("rock");
        // "ROCK" è un duplicato di "rock" in senso case-insensitive
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.createPlaylist("ROCK"));
    }

    @Test
    void createPlaylistRemainsEmptyAfterInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.createPlaylist(""));
        assertTrue(playlistService.getAllPlaylists().isEmpty());
    }

    // deletePlaylist 

    @Test
    void deleteExistingPlaylistDecreasesSize() {
        playlistService.createPlaylist("Rock");
        Playlist p = playlistService.getAllPlaylists().get(0);
        playlistService.deletePlaylist(p);
        assertEquals(0, playlistService.getAllPlaylists().size());
    }

    @Test
    void deleteExistingPlaylistIsNoLongerPresent() {
        playlistService.createPlaylist("Jazz");
        Playlist p = playlistService.getAllPlaylists().get(0);
        playlistService.deletePlaylist(p);
        assertFalse(playlistService.getAllPlaylists().contains(p));
    }

    @Test
    void deleteLeavesOtherPlaylistsIntact() {
        playlistService.createPlaylist("Rock");
        playlistService.createPlaylist("Pop");
        Playlist rock = playlistService.getAllPlaylists().get(0);
        Playlist pop  = playlistService.getAllPlaylists().get(1);
        playlistService.deletePlaylist(rock);
        assertEquals(1, playlistService.getAllPlaylists().size());
        assertTrue(playlistService.getAllPlaylists().contains(pop));
    }

    @Test
    void deleteNonExistentPlaylistDoesNotThrow() {
        Playlist externa = new Playlist("Esterna");
        assertDoesNotThrow(() -> playlistService.deletePlaylist(externa));
        assertTrue(playlistService.getAllPlaylists().isEmpty());
    }

    //  getAllPlaylists 

    @Test
    void getAllPlaylistsReturnsEmptyInitially() {
        assertTrue(playlistService.getAllPlaylists().isEmpty());
    }

    @Test
    void getAllPlaylistsReturnsSameObservableListReference() {
        assertSame(playlistService.getAllPlaylists(), playlistService.getAllPlaylists());
    }
}
