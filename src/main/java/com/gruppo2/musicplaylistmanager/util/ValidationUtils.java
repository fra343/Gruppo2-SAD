package com.gruppo2.musicplaylistmanager.util;

import java.time.Year;

/**
 * Metodi statici di validazione per i campi di una traccia.
 * Ogni metodo lancia {@link IllegalArgumentException} se il valore non rispetta
 * i vincoli; in caso contrario non fa nulla.
 */
public final class ValidationUtils {

    /** Anno minimo accettato, corrispondente all'invenzione del fonografo. */
    private static final int MIN_YEAR = 1860;

    private ValidationUtils() { }

    /**
     * Verifica che la stringa non sia nulla né composta solo da spazi.
     *
     * @param value     valore da controllare
     * @param fieldName nome del campo, usato nel messaggio di errore
     * @throws IllegalArgumentException se {@code value} è vuoto o blank
     */
    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo '" + fieldName + "' non può essere vuoto.");
        }
    }

    /**
     * Verifica che la durata sia strettamente maggiore di zero.
     *
     * @param duration durata in secondi
     * @throws IllegalArgumentException se {@code duration} è minore o uguale a zero
     */
    public static void requireValidDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("La durata deve essere maggiore di zero.");
        }
    }

    /**
     * Verifica che l'anno sia compreso tra {@value #MIN_YEAR} e l'anno corrente.
     *
     * @param year anno da controllare
     * @throws IllegalArgumentException se l'anno è fuori dall'intervallo valido
     */
    public static void requireValidYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear) {
            throw new IllegalArgumentException(
                    "L'anno deve essere compreso tra " + MIN_YEAR + " e " + currentYear + ".");
        }
    }
}
