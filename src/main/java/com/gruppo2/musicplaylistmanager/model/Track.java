package com.gruppo2.musicplaylistmanager.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// Traccia musicale della libreria; ogni istanza ha un id UUID immutabile generato alla creazione.
public class Track {

    private final UUID id;
    private String title;
    private String author;
    private int duration; // durata in secondi
    private String genre;
    private int year;
    private final Set<Tag> tags;

    public Track(String title, String author, int duration, String genre, int year) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.genre = genre;
        this.year = year;
        this.tags = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    // due tracce con gli stessi dati ma create separatamente non sono uguali
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;
        return id.equals(((Track) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title + " - " + author + " (" + year + ")";
    }
}