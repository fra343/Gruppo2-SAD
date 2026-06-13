package com.gruppo2.musicplaylistmanager.persistence;

import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Tag;
import com.gruppo2.musicplaylistmanager.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di persistenza per {@link JsonTrackDAO} e {@link JsonPlaylistDAO}.
 * Ogni metodo di test riceve una directory temporanea fresca via {@link TempDir},
 * così i file JSON non interferiscono fra un test e l'altro né con la home dell'utente.
 */
class JsonDAOTest {

    @TempDir
    Path tempDir;

    private JsonTrackDAO    trackDAO;
    private JsonPlaylistDAO playlistDAO;

    @BeforeEach
    void setUp() {
        trackDAO    = new JsonTrackDAO(tempDir.resolve("tracks.json"));
        playlistDAO = new JsonPlaylistDAO(tempDir.resolve("playlists.json"));
    }

    // -----------------------------------------------------------------------
    // JsonTrackDAO
    // -----------------------------------------------------------------------

    @Test
    void saveAndLoad_singleTrack_preservesAllFields() {
        Track t = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
        trackDAO.save(List.of(t));

        List<Track> loaded = trackDAO.load();

        assertEquals(1, loaded.size());
        Track r = loaded.get(0);
        assertEquals(t.getId(),       r.getId());
        assertEquals(t.getTitle(),    r.getTitle());
        assertEquals(t.getAuthor(),   r.getAuthor());
        assertEquals(t.getDuration(), r.getDuration());
        assertEquals(t.getGenre(),    r.getGenre());
        assertEquals(t.getYear(),     r.getYear());
    }

    @Test
    void saveAndLoad_trackWithTags_tagsPreserved() {
        Track t = new Track("Favourite Song", "Artist", 200, "Pop", 2020);
        t.getTags().add(Tag.FAVOURITE);
        t.getTags().add(Tag.EXPLICIT);
        trackDAO.save(List.of(t));

        List<Track> loaded = trackDAO.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTags().contains(Tag.FAVOURITE));
        assertTrue(loaded.get(0).getTags().contains(Tag.EXPLICIT));
        assertFalse(loaded.get(0).getTags().contains(Tag.NEW_RELEASE));
    }

    @Test
    void saveAndLoad_multipleTracks_orderAndDataPreserved() {
        Track t1 = new Track("A", "X", 100, "Rock", 2000);
        Track t2 = new Track("B", "Y", 200, "Pop",  2010);
        Track t3 = new Track("C", "Z", 300, "Jazz", 2020);
        trackDAO.save(List.of(t1, t2, t3));

        List<Track> loaded = trackDAO.load();

        assertEquals(3, loaded.size());
        assertEquals(t1.getId(), loaded.get(0).getId());
        assertEquals(t2.getId(), loaded.get(1).getId());
        assertEquals(t3.getId(), loaded.get(2).getId());
    }

    @Test
    void saveAndLoad_emptyTrackList_returnsEmptyList() {
        trackDAO.save(List.of());

        assertTrue(trackDAO.load().isEmpty());
    }

    @Test
    void load_tracks_whenFileAbsent_returnsEmptyList() {
        assertTrue(trackDAO.load().isEmpty());
    }

    @Test
    void load_tracks_whenFileEmpty_returnsEmptyList() throws IOException {
        Files.createFile(tempDir.resolve("tracks.json"));

        assertTrue(trackDAO.load().isEmpty());
    }

    // -----------------------------------------------------------------------
    // JsonPlaylistDAO
    // -----------------------------------------------------------------------

    @Test
    void saveAndLoad_singlePlaylist_preservesIdAndName() {
        Playlist pl = new Playlist("Rock Hits");
        playlistDAO.save(List.of(pl));

        List<Playlist> loaded = playlistDAO.load();

        assertEquals(1, loaded.size());
        assertEquals(pl.getId(),   loaded.get(0).getId());
        assertEquals(pl.getName(), loaded.get(0).getName());
    }

    @Test
    void saveAndLoad_playlistWithTracks_tracksPreserved() {
        Track t = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971);
        Playlist pl = new Playlist("Classic Rock");
        pl.addTrack(t);
        playlistDAO.save(List.of(pl));

        List<Playlist> loaded = playlistDAO.load();

        assertEquals(1, loaded.size());
        List<Track> tracks = loaded.get(0).getTracks();
        assertEquals(1, tracks.size());
        assertEquals(t.getId(),    tracks.get(0).getId());
        assertEquals(t.getTitle(), tracks.get(0).getTitle());
    }

    @Test
    void saveAndLoad_emptyPlaylist_preserved() {
        Playlist pl = new Playlist("Empty");
        playlistDAO.save(List.of(pl));

        List<Playlist> loaded = playlistDAO.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTracks().isEmpty());
    }

    @Test
    void saveAndLoad_multiplePlaylists_allPreserved() {
        Playlist p1 = new Playlist("Playlist A");
        Playlist p2 = new Playlist("Playlist B");
        p1.addTrack(new Track("Song 1", "Artist", 180, "Pop", 2020));
        playlistDAO.save(List.of(p1, p2));

        List<Playlist> loaded = playlistDAO.load();

        assertEquals(2, loaded.size());
        assertEquals(p1.getId(), loaded.get(0).getId());
        assertEquals(p2.getId(), loaded.get(1).getId());
    }

    @Test
    void load_playlists_whenFileAbsent_returnsEmptyList() {
        assertTrue(playlistDAO.load().isEmpty());
    }

    @Test
    void load_playlists_whenFileEmpty_returnsEmptyList() throws IOException {
        Files.createFile(tempDir.resolve("playlists.json"));

        assertTrue(playlistDAO.load().isEmpty());
    }

    // -----------------------------------------------------------------------
    // Test di persistenza integrata (save → load con tracce e playlist)
    // -----------------------------------------------------------------------

    @Test
    void fullCycle_tracksAndPlaylists_dataIntegrityPreserved() {
        Track t1 = new Track("Song A", "Artist A", 180, "Rock", 2000);
        Track t2 = new Track("Song B", "Artist B", 240, "Pop",  2005);
        t1.getTags().add(Tag.FAVOURITE);

        Playlist pl = new Playlist("Mixed");
        pl.addTrack(t1);
        pl.addTrack(t2);

        trackDAO.save(List.of(t1, t2));
        playlistDAO.save(List.of(pl));

        List<Track>    loadedTracks    = trackDAO.load();
        List<Playlist> loadedPlaylists = playlistDAO.load();

        assertEquals(2, loadedTracks.size());
        assertEquals(1, loadedPlaylists.size());

        Playlist loadedPl = loadedPlaylists.get(0);
        assertEquals(pl.getName(), loadedPl.getName());
        assertEquals(2, loadedPl.getTracks().size());

        // Verifica che i tag della traccia siano stati preservati nel round-trip
        Track loadedT1 = loadedTracks.stream()
                .filter(t -> t.getId().equals(t1.getId()))
                .findFirst()
                .orElseThrow();
        assertTrue(loadedT1.getTags().contains(Tag.FAVOURITE));
    }
}
