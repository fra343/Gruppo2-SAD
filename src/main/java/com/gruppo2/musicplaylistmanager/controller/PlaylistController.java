package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.command.AddTrackToPlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.CommandHistory;
import com.gruppo2.musicplaylistmanager.command.CreatePlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.DeletePlaylistCommand;
import com.gruppo2.musicplaylistmanager.command.MoveTrackCommand;
import com.gruppo2.musicplaylistmanager.command.RemoveTrackFromPlaylistCommand;
import com.gruppo2.musicplaylistmanager.model.Playlist;
import com.gruppo2.musicplaylistmanager.model.Tag;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller per la gestione delle playlist: creazione, eliminazione,
 * aggiunta e rimozione di tracce da una playlist selezionata.
 * <p>
 * Visualizza le playlist come card cliccabili nel pannello destro e,
 * quando una card è selezionata, mostra un pannello di dettaglio con
 * le tracce contenute e i pulsanti di aggiunta/rimozione.
 * </p>
 * Le operazioni di modifica (aggiunta e rimozione di tracce) vengono
 * incapsulate in oggetti {@link com.gruppo2.musicplaylistmanager.command.Command}
 * e affidate alla {@link CommandHistory} condivisa, in modo che l'undo
 * possa essere abilitato in US-12 senza modificare questo controller.
 */
public class PlaylistController {

    private static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");

    private PlaylistService  playlistService;
    private LibraryService   libraryService;
    private PlaybackService  playbackService;
    private CommandHistory   commandHistory;
    private FlowPane         playlistFlowPane;
    private TableView<Track> libraryTableView;
    private VBox             playlistDetailPane;
    private Label            playlistDetailLabel;
    private ListView<Track>  playlistTrackListView;

    /** Playlist attualmente selezionata tramite click su una card, o {@code null}. */
    private Playlist selectedPlaylist = null;

    /**
     * Inietta il servizio delle playlist.
     *
     * @param playlistService servizio condiviso con gli altri controller
     */
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * Inietta il servizio della libreria, usato dalla generazione automatica di
     * playlist per interrogare le tracce per genere e anno (US-14a / US-14b).
     *
     * @param libraryService servizio condiviso con gli altri controller
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Inietta il servizio di riproduzione condiviso, necessario per notificare
     * la rimozione di una traccia durante la riproduzione .
     *
     * @param playbackService servizio di riproduzione condiviso
     */
    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    /**
     * Inietta la {@link CommandHistory} condivisa, usata per registrare
     * le operazioni di aggiunta e rimozione di tracce.
     *
     * @param commandHistory storia dei comandi condivisa con gli altri controller
     */
    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * Inietta la TableView della libreria, necessaria per leggere
     * la traccia selezionata al momento dell'aggiunta a una playlist.
     *
     * @param libraryTableView tabella delle tracce della libreria
     */
    public void setLibraryTableView(TableView<Track> libraryTableView) {
        this.libraryTableView = libraryTableView;
    }

    /**
     * Collega il {@link FlowPane} al controller e registra un listener
     * sull'ObservableList delle playlist: ogni modifica alla lista
     * ricostruisce automaticamente le card nel pannello.
     *
     * @param playlistFlowPane pannello in cui vengono visualizzate le card
     */
    public void setPlaylistFlowPane(FlowPane playlistFlowPane) {
        this.playlistFlowPane = playlistFlowPane;
        playlistService.getAllPlaylists().addListener(
                (ListChangeListener<Playlist>) change -> rebuildCards());
    }

    /**
     * Inietta i componenti del pannello di dettaglio della playlist selezionata:
     * il contenitore (inizialmente nascosto), l'etichetta con il nome della
     * playlist e la ListView delle tracce contenute.
     *
     * @param pane     VBox contenitore del dettaglio
     * @param label    Label che mostra "Tracce in: &lt;nome&gt;"
     * @param listView ListView collegata all'ObservableList della playlist
     */
    public void setPlaylistDetail(VBox pane, Label label, ListView<Track> listView) {
        this.playlistDetailPane    = pane;
        this.playlistDetailLabel   = label;
        this.playlistTrackListView = listView;

        listView.setCellFactory(view -> new ListCell<>() {
            private final Button playBtn = new Button("▶");
            private final InvalidationListener hl = obs -> updateHighlight();

            {
                playBtn.getStyleClass().add("btn-play-inline");
                playBtn.setOnAction(e -> {
                    if (getItem() != null && selectedPlaylist != null)
                        playbackService.play(selectedPlaylist, getItem());
                });
                playbackService.currentTrackProperty()
                        .addListener(new WeakInvalidationListener(hl));
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
                updateHighlight();
            }

            private void updateHighlight() {
                Track current = playbackService.currentTrackProperty().get();
                pseudoClassStateChanged(CURRENT,
                        !isEmpty() && getItem() != null && getItem().equals(current));
            }
        });
    }

    /**
     * @return la playlist attualmente selezionata, o {@code null} se nessuna
     *         card è selezionata
     */
    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    /**
     * Apre un dialog per inserire il nome e crea una nuova playlist tramite
     * {@link PlaylistService#createPlaylist}. In caso di errore mostra un alert.
     * La lista delle card si aggiorna automaticamente tramite il listener.
     */
    public void onCreatePlaylist() {
        TextField nameField = new TextField();
        nameField.setPromptText("Nome della playlist");

        VBox content = new VBox(8, nameField);
        content.setStyle("-fx-padding: 10;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova playlist");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(e -> nameField.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                commandHistory.push(new CreatePlaylistCommand(playlistService, nameField.getText()));
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Elimina la playlist attualmente selezionata tramite
     * {@link PlaylistService#deletePlaylist}, previa conferma dell'utente.
     * Mostra un errore se nessuna playlist è selezionata.
     * La lista delle card si aggiorna automaticamente tramite il listener.
     */
    public void onDeletePlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona una playlist da eliminare.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Eliminare la playlist \"" + selectedPlaylist.getName() + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (playbackService != null && selectedPlaylist.equals(playbackService.getCurrentPlaylist())) {
                playbackService.notifyPlaylistRemoved(selectedPlaylist);
            }
            commandHistory.push(new DeletePlaylistCommand(playlistService, selectedPlaylist));
            selectedPlaylist = null;
        }
    }

    /**
     * US-14a: genera una playlist con tutte le tracce di un genere scelto.
     * Apre un dialog con i generi presenti in libreria (selezionabili o digitabili);
     * se per il genere indicato non esistono tracce mostra un messaggio informativo
     * e non crea alcuna playlist.
     */
    public void onGenerateByGenre() {
        askChoice("Genera playlist per genere", "Genere:", distinctGenres())
                .ifPresent(genre -> generate("Genere: " + genre, libraryService.findByGenre(genre)));
    }

    /**
     * US-14b: genera una playlist con tutte le tracce di un anno scelto, riusando
     * il flusso della US-14a. Apre un dialog con gli anni presenti in libreria;
     * gestisce anno non numerico e assenza di tracce con un messaggio informativo,
     * senza creare alcuna playlist.
     */
    public void onGenerateByYear() {
        Optional<String> choice = askChoice("Genera playlist per anno", "Anno:", distinctYears());
        if (choice.isEmpty()) {
            return;
        }
        int year;
        try {
            year = Integer.parseInt(choice.get());
        } catch (NumberFormatException ex) {
            showError("Inserisci un anno valido (numero intero).");
            return;
        }
        generate("Anno: " + year, libraryService.findByYear(year));
    }

    /**
     * US-16: genera una playlist con tutte le tracce che hanno il tag scelto,
     * riusando il flusso di generazione di US-14a/US-14b. Apre un dialog di scelta
     * fra i tre tag visivi; il nome della playlist dipende dal tag selezionato
     * (es. "I miei Preferiti" per {@link Tag#FAVOURITE}). Se nessuna traccia ha il
     * tag mostra un messaggio informativo e non crea alcuna playlist.
     */
    public void onGenerateByTag() {
        askTag().ifPresent(tag ->
                generate(playlistNameForTag(tag), libraryService.findByTag(tag)));
    }

    /**
     * Mostra un dialog con un menu a tendina dei tag visivi (etichette leggibili)
     * e restituisce il tag scelto, oppure vuoto se l'utente annulla.
     *
     * @return il tag selezionato, oppure {@link Optional#empty()}
     */
    private Optional<Tag> askTag() {
        ComboBox<Tag> combo = new ComboBox<>();
        combo.getItems().addAll(Tag.values());
        combo.setConverter(new StringConverter<>() {
            @Override public String toString(Tag tag) { return tag == null ? "" : tag.getLabel(); }
            @Override public Tag fromString(String s) { return null; }
        });
        combo.getSelectionModel().selectFirst();
        combo.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(8, new Label("Tag:"), combo);
        content.setStyle("-fx-padding: 10;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Genera playlist per tag");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(e -> combo.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return Optional.ofNullable(combo.getValue());
        }
        return Optional.empty();
    }

    /**
     * Restituisce il nome della playlist generata per il tag indicato.
     *
     * @param tag tag scelto per la generazione
     * @return nome descrittivo della playlist
     */
    private String playlistNameForTag(Tag tag) {
        return switch (tag) {
            case FAVOURITE   -> "I miei Preferiti";
            case EXPLICIT    -> "Brani Espliciti";
            case NEW_RELEASE -> "Nuove Uscite";
        };
    }

    /**
     * Crea la playlist automatica con le tracce fornite riusando
     * {@link PlaylistService#generateAutoPlaylist}: se la lista è vuota mostra il
     * messaggio dell'eccezione e non crea nulla. La nuova playlist compare da sé
     * nel pannello grazie al listener registrato sull'ObservableList.
     *
     * @param name   nome della playlist da generare
     * @param tracks tracce ottenute dal criterio di ricerca
     */
    private void generate(String name, List<Track> tracks) {
        try {
            playlistService.generateAutoPlaylist(name, tracks);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /** @return i generi distinti delle tracce in libreria, ordinati alfabeticamente. */
    private List<String> distinctGenres() {
        return libraryService.getAllTracks().stream()
                .map(Track::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /** @return gli anni distinti delle tracce in libreria, ordinati. */
    private List<String> distinctYears() {
        return libraryService.getAllTracks().stream()
                .map(t -> String.valueOf(t.getYear()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Mostra un dialog con un menu a tendina editabile dei valori forniti e
     * restituisce il valore scelto o digitato, oppure vuoto se l'utente annulla
     * o lascia il campo vuoto.
     *
     * @param title     titolo del dialog
     * @param labelText etichetta del campo
     * @param options   valori proposti nel menu a tendina
     * @return il valore selezionato/digitato, oppure {@link Optional#empty()}
     */
    private Optional<String> askChoice(String title, String labelText, List<String> options) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(options);
        combo.setEditable(true);
        combo.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(8, new Label(labelText), combo);
        content.setStyle("-fx-padding: 10;");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(e -> combo.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String value = combo.getEditor().getText().trim();
            return value.isEmpty() ? Optional.empty() : Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * Aggiunge la traccia selezionata nella libreria alla playlist corrente.
     * L'operazione viene incapsulata in un {@link AddTrackToPlaylistCommand}
     * e affidata alla {@link CommandHistory}.
     * Mostra un errore se nessuna playlist o nessuna traccia è selezionata,
     * o se la traccia è già presente nella playlist.
     */
    public void onAddToPlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona prima una playlist.");
            return;
        }
        Track selectedTrack = libraryTableView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            showError("Seleziona una traccia dalla libreria.");
            return;
        }
        try {
            commandHistory.push(
                    new AddTrackToPlaylistCommand(playlistService, playbackService, selectedPlaylist, selectedTrack));
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Sposta verso l'alto (indice - 1) la traccia selezionata nella ListView.
     * Non produce effetti se la traccia è già in prima posizione o nessuna è selezionata.
     */
    public void onMoveUp() {
        if (selectedPlaylist == null) return;
        Track selected = playlistTrackListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        int idx = selectedPlaylist.getTracks().indexOf(selected);
        if (idx <= 0) return;
        commandHistory.push(new MoveTrackCommand(playlistService, selectedPlaylist, idx, idx - 1));
        playlistTrackListView.getSelectionModel().select(idx - 1);
    }

    /**
     * Sposta verso il basso (indice + 1) la traccia selezionata nella ListView.
     * Non produce effetti se la traccia è già in ultima posizione o nessuna è selezionata.
     */
    public void onMoveDown() {
        if (selectedPlaylist == null) return;
        Track selected = playlistTrackListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        int idx = selectedPlaylist.getTracks().indexOf(selected);
        if (idx < 0 || idx >= selectedPlaylist.getTracks().size() - 1) return;
        commandHistory.push(new MoveTrackCommand(playlistService, selectedPlaylist, idx, idx + 1));
        playlistTrackListView.getSelectionModel().select(idx + 1);
    }

    /**
     * Rimuove la traccia selezionata nel pannello di dettaglio dalla playlist
     * corrente. L'operazione viene incapsulata in un
     * {@link RemoveTrackFromPlaylistCommand} e affidata alla {@link CommandHistory}.
     * Mostra un errore se nessuna playlist o nessuna traccia è selezionata.
     */
    public void onRemoveFromPlaylist() {
        if (selectedPlaylist == null) {
            showError("Seleziona prima una playlist.");
            return;
        }
        Track selectedTrack = playlistTrackListView.getSelectionModel().getSelectedItem();
        if (selectedTrack == null) {
            showError("Seleziona una traccia dalla playlist da rimuovere.");
            return;
        }
        // Notifica prima della rimozione: la traccia è ancora nella playlist,
        // quindi la strategia può calcolarne la posizione corretta.
        if (playbackService != null && selectedPlaylist.equals(playbackService.getCurrentPlaylist())) {
            playbackService.notifyTrackRemoved(selectedTrack);
        }
        commandHistory.push(
                new RemoveTrackFromPlaylistCommand(playlistService, selectedPlaylist, selectedTrack));
    }

    /**
     * Ricostruisce tutte le card nel pannello partendo dalla lista corrente
     * delle playlist, azzera la selezione e nasconde il pannello di dettaglio.
     * Viene invocato automaticamente dal listener ogni volta che la lista cambia.
     */
    private void rebuildCards() {
        playlistFlowPane.getChildren().clear();
        selectedPlaylist = null;
        hidePlaylistDetail();
        for (Playlist p : playlistService.getAllPlaylists()) {
            playlistFlowPane.getChildren().add(createCard(p));
        }
    }

    /**
     * Crea la card visiva per una singola playlist.
     * Il click seleziona la card, la evidenzia e mostra il pannello di dettaglio.
     *
     * @param playlist playlist da rappresentare
     * @return nodo JavaFX che rappresenta la card
     */
    private Node createCard(Playlist playlist) {
        Label nameLabel = new Label(playlist.getName());
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"
                + " -fx-text-fill: #333333; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(98);

        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("btn-play-inline");
        playBtn.setOnAction(e -> playbackService.play(playlist));

        HBox topBar = new HBox(playBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox card = new VBox(topBar, spacer, nameLabel);
        card.setPrefWidth(110);
        card.setPrefHeight(90);
        card.setStyle(styleCard(false));

        card.setOnMouseClicked(e -> {
            if (playlist.equals(selectedPlaylist)) {
                card.setStyle(styleCard(false));
                selectedPlaylist = null;
                hidePlaylistDetail();
            } else {
                resetAllCards();
                card.setStyle(styleCard(true));
                selectedPlaylist = playlist;
                showPlaylistDetail(playlist);
            }
        });

        return card;
    }

    /**
     * Mostra il pannello di dettaglio per la playlist indicata:
     * aggiorna l'etichetta e collega la ListView all'ObservableList della playlist.
     * Il binding diretto fa sì che la vista si aggiorni automaticamente
     * a ogni aggiunta o rimozione di tracce.
     *
     * @param playlist playlist di cui mostrare il contenuto
     */
    private void showPlaylistDetail(Playlist playlist) {
        if (playlistDetailPane == null) return;
        playlistDetailLabel.setText("Tracce in: " + playlist.getName());
        playlistTrackListView.setItems(playlist.getTracks());
        playlistDetailPane.setVisible(true);
        playlistDetailPane.setManaged(true);
    }

    /**
     * Nasconde il pannello di dettaglio e scollega la ListView
     * dall'eventuale ObservableList precedente.
     */
    private void hidePlaylistDetail() {
        if (playlistDetailPane == null) return;
        playlistDetailPane.setVisible(false);
        playlistDetailPane.setManaged(false);
        playlistTrackListView.setItems(null);
    }

    /** Rimuove la selezione visiva da tutte le card. */
    private void resetAllCards() {
        for (Node node : playlistFlowPane.getChildren()) {
            node.setStyle(styleCard(false));
        }
    }

    private String styleCard(boolean selected) {
        String base = "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8;";
        return selected
                ? base + " -fx-background-color: #C8C8C8;"
                        + " -fx-border-color: #888; -fx-border-radius: 8; -fx-border-width: 2;"
                : base + " -fx-background-color: #E8E8E8;";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
