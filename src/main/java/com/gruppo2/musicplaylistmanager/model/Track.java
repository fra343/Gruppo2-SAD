package com.gruppo2.musicplaylistmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Traccia musicale della libreria.
 * Ogni istanza ha un identificatore UUID immutabile generato alla creazione,
 * quindi due tracce create separatamente non sono mai uguali anche se hanno
 * gli stessi dati.
 */
public class Track {

    private final UUID id;
    private String title;
    private String author;
    private int duration; // durata in secondi
    private String genre;
    private int year;
    private final Set<Tag> tags;

    /**
     * Crea una nuova traccia con i dati forniti e un UUID generato automaticamente.
     *
     * @param title    titolo del brano (non vuoto)
     * @param author   nome dell'artista (non vuoto)
     * @param duration durata in secondi (maggiore di zero)
     * @param genre    genere musicale (non vuoto)
     * @param year     anno di pubblicazione
     */
    public Track(String title, String author, int duration, String genre, int year) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.genre = genre;
        this.year = year;
        this.tags = new HashSet<>();
    }

    /**
     * Ricostruisce una traccia con UUID e tag preesistenti dalla deserializzazione JSON.
     */
    @JsonCreator
    public Track(
            @JsonProperty("id")       UUID   id,
            @JsonProperty("title")    String title,
            @JsonProperty("author")   String author,
            @JsonProperty("duration") int    duration,
            @JsonProperty("genre")    String genre,
            @JsonProperty("year")     int    year,
            @JsonProperty("tags")     Set<Tag> tags) {
        this.id       = id != null ? id : UUID.randomUUID();
        this.title    = title;
        this.author   = author;
        this.duration = duration;
        this.genre    = genre;
        this.year     = year;
        this.tags     = tags != null ? new HashSet<>(tags) : new HashSet<>();
    }

    /** @return identificatore UUID immutabile della traccia */
    public UUID getId() {
        return id;
    }

    /** @return titolo del brano */
    public String getTitle() {
        return title;
    }

    /** @return nome dell'artista */
    public String getAuthor() {
        return author;
    }

    /** @return durata in secondi */
    public int getDuration() {
        return duration;
    }

    /** @return genere musicale */
    public String getGenre() {
        return genre;
    }

    /** @return anno di pubblicazione */
    public int getYear() {
        return year;
    }

    /** @return insieme dei tag associati alla traccia */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Sostituisce il titolo della traccia.
     *
     * @param title nuovo titolo (non vuoto)
     */
    public void setTitle(String title)      { this.title = title; }

    /**
     * Sostituisce il nome dell'artista.
     *
     * @param author nuovo autore (non vuoto)
     */
    public void setAuthor(String author)    { this.author = author; }

    /**
     * Aggiorna la durata della traccia.
     *
     * @param duration nuova durata in secondi (maggiore di zero)
     */
    public void setDuration(int duration)   { this.duration = duration; }

    /**
     * Aggiorna il genere musicale.
     *
     * @param genre nuovo genere (non vuoto)
     */
    public void setGenre(String genre)      { this.genre = genre; }

    /**
     * Aggiorna l'anno di pubblicazione.
     *
     * @param year nuovo anno
     */
    public void setYear(int year)           { this.year = year; }

    /**
     * Due tracce sono uguali se e solo se condividono lo stesso UUID.
     * Due tracce con gli stessi dati ma create separatamente non sono uguali.
     */
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

    /** @return stringa nel formato "titolo - autore (anno)" */
    @Override
    public String toString() {
        return title + " - " + author + " (" + year + ")";
    }
}
