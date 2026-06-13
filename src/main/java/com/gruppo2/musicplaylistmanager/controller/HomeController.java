package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller della Home page (US-13), organizzata in tre blocchi:
 * <ol>
 *   <li><b>Top Playlist</b>: le playlist più ascoltate, mostrate come card
 *       ordinate per numero di avvii decrescente e cliccabili;</li>
 *   <li><b>Dettaglio</b>: le tracce della playlist selezionata nel primo blocco;</li>
 *   <li><b>Top Tracks</b>: tutte le tracce della libreria, con le più riprodotte
 *       in cima.</li>
 * </ol>
 * I conteggi di riproduzione sono di sessione e vengono letti dal
 * {@link PlaybackService}; la logica di ordinamento risiede nel service
 * (testabile senza UI) mentre questo controller ne presenta il risultato.
 */
public class HomeController {

    @FXML private VBox            topPlaylistsPane;
    @FXML private Label           noPlaylistsLabel;
    @FXML private Label           selectedPlaylistTitle;
    @FXML private ListView<Track> selectedPlaylistTracksView;
    @FXML private ListView<Track> topTracksListView;

    private Library         library;
    private PlaybackService playbackService;
    private Runnable        onBackToLibrary;

    /** Card attualmente selezionata nel blocco "Top Playlist", o {@code null}. */
    private Node selectedCard;

    /** Playlist selezionata di cui il blocco centrale mostra le tracce, o {@code null}. */
    private Playlist selectedPlaylist;

    /**
     * Inietta le dipendenze necessarie al controller e configura la cell factory
     * del blocco "Top Tracks", che affianca a ogni traccia il relativo numero di
     * riproduzioni quando maggiore di zero.
     *
     * @param library         libreria condivisa con l'applicazione
     * @param playbackService servizio di riproduzione da cui leggere i conteggi
     */
    public void setDependencies(Library library, PlaybackService playbackService) {
        this.library         = library;
        this.playbackService = playbackService;
        configureSelectedPlaylistCells();
        configureTopTracksCells();
    }

    /**
     * Inietta il callback invocato quando l'utente chiede di tornare alla vista
     * principale (freccia "↩"). La navigazione effettiva è gestita dal
     * {@link MainController}, che possiede le viste da scambiare.
     *
     * @param onBackToLibrary azione da eseguire al ritorno verso la libreria
     */
    public void setOnBackToLibrary(Runnable onBackToLibrary) {
        this.onBackToLibrary = onBackToLibrary;
    }

    /**
     * Ricalcola e mostra i tre blocchi della home: ricostruisce le card delle
     * playlist più ascoltate, popola la lista di tutte le tracce ordinata per
     * riproduzioni e azzera il dettaglio della playlist selezionata.
     * Va invocato a ogni ingresso nella Home, così da riflettere i conteggi
     * aggiornati della sessione.
     */
    public void refresh() {
        List<Playlist> topPlaylists = playbackService.getMostPlayedPlaylists(library.getPlaylists());
        rebuildPlaylistCards(topPlaylists);

        topTracksListView.getItems().setAll(playbackService.getTracksByPlayCountDesc(library.getTracks()));

        clearSelectedPlaylist();
    }

    /** Larghezza fissa della card "stretta" nella disposizione a puzzle. */
    private static final double NARROW_CARD_WIDTH = 150;

    /**
     * Ricostruisce le card del blocco "Top Playlist" a partire dalle playlist
     * già ordinate per numero di avvii decrescente, numerandole per posizione.
     * Le dispone a puzzle: due card per riga con larghezza alternata (larga a
     * sinistra nelle righe pari, a destra in quelle dispari); un'eventuale card
     * dispari finale occupa l'intera riga. Mostra la label informativa quando non
     * ci sono playlist ascoltate.
     *
     * @param playlists playlist più ascoltate, in ordine decrescente
     */
    private void rebuildPlaylistCards(List<Playlist> playlists) {
        topPlaylistsPane.getChildren().clear();
        selectedCard = null;
        boolean empty = playlists.isEmpty();
        noPlaylistsLabel.setVisible(empty);
        noPlaylistsLabel.setManaged(empty);

        for (int i = 0; i < playlists.size(); i += 2) {
            HBox row = new HBox(14);
            VBox left = createCard(playlists.get(i), i + 1);
            if (i + 1 < playlists.size()) {
                VBox right = createCard(playlists.get(i + 1), i + 2);
                boolean leftWide = (i / 2) % 2 == 0;
                stretch(leftWide ? left : right);
                fixWidth(leftWide ? right : left);
                row.getChildren().addAll(left, right);
            } else {
                stretch(left); // card dispari finale: occupa tutta la riga
                row.getChildren().add(left);
            }
            topPlaylistsPane.getChildren().add(row);
        }
    }

    /** Rende la card elastica, così da occupare lo spazio orizzontale residuo della riga. */
    private void stretch(VBox card) {
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
    }

    /** Fissa la card alla larghezza ridotta {@link #NARROW_CARD_WIDTH}. */
    private void fixWidth(VBox card) {
        card.setMinWidth(NARROW_CARD_WIDTH);
        card.setPrefWidth(NARROW_CARD_WIDTH);
        card.setMaxWidth(NARROW_CARD_WIDTH);
    }

    /**
     * Crea la card di una playlist nel blocco "Top Playlist": posizione in
     * classifica, pulsante di avvio, nome e numero di ascolti. Il click sul
     * corpo della card seleziona la playlist e ne mostra le tracce nel blocco
     * centrale.
     *
     * @param playlist playlist da rappresentare
     * @param rank     posizione in classifica (1-based)
     * @return card JavaFX che rappresenta la playlist
     */
    private VBox createCard(Playlist playlist, int rank) {
        Label rankLabel = new Label("#" + rank);
        rankLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1F1F22;");

        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("btn-play-inline");
        playBtn.setOnAction(e -> playbackService.play(playlist));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(rankLabel, spacer, playBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label countLabel = new Label("Ascolti: " + playbackService.getPlaylistPlayCount(playlist));
        countLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8A8A90;");

        VBox card = new VBox(6, topBar, nameLabel, countLabel);
        card.setPrefHeight(90);
        card.setMinHeight(90);
        card.setStyle(cardStyle(false));
        card.setOnMouseClicked(e -> selectCard(card, playlist));
        return card;
    }

    /**
     * Seleziona una card: ne evidenzia il bordo (deselezionando la precedente)
     * e mostra le tracce della relativa playlist nel blocco centrale.
     *
     * @param card     card cliccata
     * @param playlist playlist associata alla card
     */
    private void selectCard(Node card, Playlist playlist) {
        if (selectedCard != null) {
            selectedCard.setStyle(cardStyle(false));
        }
        card.setStyle(cardStyle(true));
        selectedCard = card;
        selectedPlaylist = playlist;
        selectedPlaylistTitle.setText("Titolo: " + playlist.getName());
        selectedPlaylistTracksView.setItems(playlist.getTracks());
    }

    /** Azzera il blocco centrale quando nessuna playlist è selezionata. */
    private void clearSelectedPlaylist() {
        selectedCard = null;
        selectedPlaylist = null;
        selectedPlaylistTitle.setText("Seleziona una playlist");
        selectedPlaylistTracksView.setItems(null);
    }

    /**
     * Configura le celle del blocco centrale: ogni traccia mostra un pulsante ▶
     * che avvia la playlist selezionata a partire da quella traccia, proseguendo
     * con le successive secondo la strategia di riproduzione corrente.
     */
    private void configureSelectedPlaylistCells() {
        selectedPlaylistTracksView.setCellFactory(view -> new ListCell<>() {
            private final Button playBtn = new Button("▶");
            {
                playBtn.getStyleClass().add("btn-play-inline");
                playBtn.setOnAction(e -> {
                    if (getItem() != null && selectedPlaylist != null) {
                        playbackService.play(selectedPlaylist, getItem());
                    }
                });
            }
            @Override
            protected void updateItem(Track item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setGraphic(playBtn);
                }
            }
        });
    }

    /**
     * Configura il rendering delle celle del blocco "Top Tracks": ogni riga mostra
     * un pulsante ▶ che avvia la singola traccia, la sua descrizione e il numero di
     * riproduzioni, omesso quando la traccia non è mai stata riprodotta.
     */
    private void configureTopTracksCells() {
        topTracksListView.setCellFactory(view -> new ListCell<>() {
            private final Button playBtn = new Button("▶");
            {
                playBtn.getStyleClass().add("btn-play-inline");
                playBtn.setOnAction(e -> {
                    if (getItem() != null) {
                        playbackService.play(getItem());
                    }
                });
            }
            @Override
            protected void updateItem(Track item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int count = playbackService.getTrackPlayCount(item);
                    setText(count > 0 ? item + "  —  " + count + " ascolti" : item.toString());
                    setGraphic(playBtn);
                }
            }
        });
    }

    /** Stile inline di una card, evidenziata quando {@code selected} è {@code true}. */
    private String cardStyle(boolean selected) {
        String base = "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8;";
        return selected
                ? base + " -fx-background-color: #C8C8C8;"
                        + " -fx-border-color: #888; -fx-border-radius: 8; -fx-border-width: 2;"
                : base + " -fx-background-color: #E8E8E8;";
    }

    /**
     * Gestore della freccia "↩": delega al callback di navigazione iniettato
     * dal {@link MainController}, se presente.
     */
    @FXML
    private void onShowLibrary() {
        if (onBackToLibrary != null) {
            onBackToLibrary.run();
        }
    }
}
