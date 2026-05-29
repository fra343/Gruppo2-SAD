package com.gruppo2.musicplaylistmanager.util;

import java.time.Year;

// Metodi statici di validazione per i dati di una traccia; lancia IllegalArgumentException se il valore non è valido.
public final class ValidationUtils {

    // Anno minimo accettato per le track
    private static final int MIN_YEAR = 1860; 

    private ValidationUtils() {
        
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo '" + fieldName + "' non può essere vuoto.");
        }
    }

    public static void requireValidDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("La durata deve essere maggiore di zero.");
        }
    }

    /** Verifica che l'anno sia in un intervallo plausibile. */
    public static void requireValidYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear) {
            throw new IllegalArgumentException(
                    "L'anno deve essere compreso tra " + MIN_YEAR + " e " + currentYear + ".");
        }
    }
}