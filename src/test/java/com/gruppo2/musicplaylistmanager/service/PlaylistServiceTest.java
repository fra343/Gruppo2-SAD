package com.gruppo2.musicplaylistmanager.service;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


//Test per PlaylistService
class PlaylistServiceTest {

    private PlaylistService playlistService;

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService(new Library());
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

    // ===== Helpers =====

    private static Track validTrack() {
        return new Track("Title", "Author", 180, "Pop", 2000);
    }

    private Playlist firstPlaylist(String name) {
        playlistService.createPlaylist(name);
        return playlistService.getAllPlaylists().get(0);
    }

    // ===== addTrackToPlaylist =====

    @Test
    void addTrackToPlaylistIncreasesSize() {
        Playlist p = firstPlaylist("Rock");
        playlistService.addTrackToPlaylist(p, validTrack());
        assertEquals(1, p.getTracks().size());
    }

    @Test
    void addTrackToPlaylistTrackAppearsInPlaylist() {
        Playlist p = firstPlaylist("Rock");
        Track t = validTrack();
        playlistService.addTrackToPlaylist(p, t);
        assertTrue(p.getTracks().contains(t));
    }

    @Test
    void addTrackToPlaylistMultipleTracksAllPresent() {
        Playlist p = firstPlaylist("Rock");
        Track t1 = validTrack();
        Track t2 = new Track("Other", "Artist", 200, "Jazz", 2005);
        playlistService.addTrackToPlaylist(p, t1);
        playlistService.addTrackToPlaylist(p, t2);
        assertEquals(2, p.getTracks().size());
        assertTrue(p.getTracks().contains(t1));
        assertTrue(p.getTracks().contains(t2));
    }

    @Test
    void addTrackToPlaylistDuplicateThrows() {
        Playlist p = firstPlaylist("Rock");
        Track t = validTrack();
        playlistService.addTrackToPlaylist(p, t);
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.addTrackToPlaylist(p, t));
    }

    @Test
    void addTrackToPlaylistDuplicateDoesNotModifyPlaylist() {
        Playlist p = firstPlaylist("Rock");
        Track t = validTrack();
        playlistService.addTrackToPlaylist(p, t);
        assertThrows(IllegalArgumentException.class,
                () -> playlistService.addTrackToPlaylist(p, t));
        assertEquals(1, p.getTracks().size());
    }

    // ===== removeTrackFromPlaylist =====

    @Test
    void removeTrackFromPlaylistDecreasesSize() {
        Playlist p = firstPlaylist("Jazz");
        Track t = validTrack();
        playlistService.addTrackToPlaylist(p, t);
        playlistService.removeTrackFromPlaylist(p, t);
        assertEquals(0, p.getTracks().size());
    }

    @Test
    void removeTrackFromPlaylistTrackNoLongerPresent() {
        Playlist p = firstPlaylist("Jazz");
        Track t = validTrack();
        playlistService.addTrackToPlaylist(p, t);
        playlistService.removeTrackFromPlaylist(p, t);
        assertFalse(p.getTracks().contains(t));
    }

    @Test
    void removeTrackFromPlaylistLeavesOtherTracksIntact() {
        Playlist p = firstPlaylist("Jazz");
        Track t1 = validTrack();
        Track t2 = new Track("Other", "Artist", 200, "Rock", 2005);
        playlistService.addTrackToPlaylist(p, t1);
        playlistService.addTrackToPlaylist(p, t2);
        playlistService.removeTrackFromPlaylist(p, t1);
        assertEquals(1, p.getTracks().size());
        assertTrue(p.getTracks().contains(t2));
    }

    @Test
    void removeNonPresentTrackFromPlaylistDoesNotThrow() {
        Playlist p = firstPlaylist("Jazz");
        assertDoesNotThrow(() -> playlistService.removeTrackFromPlaylist(p, validTrack()));
    }
}
