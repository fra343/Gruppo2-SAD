package com.gruppo2.musicplaylistmanager.model;

/**
 * Etichette visive applicabili a una traccia (US-15).
 * Ogni tag espone un'etichetta leggibile ({@link #getLabel()}) usata nei
 * controlli e nei dialog dell'interfaccia. La resa grafica dei tag accanto
 * alla traccia (icona e stato attivo/non attivo) è responsabilità del layer
 * di presentazione.
 */
public enum Tag {
    FAVOURITE("Preferito"),
    EXPLICIT("Esplicito"),
    NEW_RELEASE("Nuova uscita");

    private final String label;

    Tag(String label) {
        this.label = label;
    }

    /** @return etichetta leggibile del tag (es. "Preferito"). */
    public String getLabel() {
        return label;
    }
}
