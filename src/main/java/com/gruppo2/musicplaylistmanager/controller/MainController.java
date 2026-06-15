package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.command.CommandHistory;
import com.gruppo2.musicplaylistmanager.model.Library;
import com.gruppo2.musicplaylistmanager.model.Tag;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.persistence.JsonPlaylistDAO;
import com.gruppo2.musicplaylistmanager.persistence.JsonTrackDAO;
import com.gruppo2.musicplaylistmanager.persistence.PlaylistDAO;
import com.gruppo2.musicplaylistmanager.persistence.TrackDAO;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import com.gruppo2.musicplaylistmanager.service.PlaybackService;
import com.gruppo2.musicplaylistmanager.service.PlaylistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller principale dell'applicazione.
 * Crea l'unica istanza di {@link Library} e di {@link CommandHistory}
 * condivise tra tutti i servizi e i controller, istanzia
 * {@link LibraryService} e {@link PlaylistService} passando loro la libreria,
 * e coordina {@link TrackListController} e {@link PlaylistController}.
 */
public class MainController {

    private final TrackListController trackListController = new TrackListController();
    private final PlaylistController  playlistController  = new PlaylistController();
    private CommandHistory            commandHistory;

    // Campi necessari per la persistenza (US-18a/18b): i service ricevono i DAO e
    // sono gli unici responsabili del caricamento/salvataggio.
    private LibraryService  libraryService;
    private PlaylistService playlistService;

    @FXML private NowPlayingCtrl             nowPlayingController;
    @FXML private HomeController             homeController;
    @FXML private HBox                       mainView;
    @FXML private VBox                       home;
    @FXML private Button                     undoButton;
    @FXML private TableView<Track>           trackTableView;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, Void>   tagColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, String> genreColumn;
    @FXML private TableColumn<Track, String> yearColumn;
    @FXML private TableColumn<Track, String> durationColumn;
    @FXML private FlowPane                   playlistFlowPane;
    @FXML private VBox                       playlistDetailPane;
    @FXML private Label                      playlistDetailLabel;
    @FXML private ListView<Track>            playlistTrackListView;

    /**
     * Invocato da FXMLLoader al caricamento della vista.
     * Crea {@link Library} e {@link CommandHistory} condivise,
     * configura i service, il binding della TableView e
     * passa le dipendenze ai sub-controller.
     */
    @FXML
    public void initialize() {
        Library library = new Library();
        commandHistory  = new CommandHistory();
        // US-12: il pulsante Annulla è abilitato solo quando c'è qualcosa da annullare.
        undoButton.disableProperty().bind(commandHistory.canUndoProperty().not());

        PlaybackService playbackService = new PlaybackService();
        nowPlayingController.setPlaybackService(playbackService);
        nowPlayingController.setSelectedPlaylistSupplier(playlistController::getSelectedPlaylist);
        // US-11: evidenziazione nella tabella della libreria (la playlist ListView è gestita da PlaylistController).
        nowPlayingController.highlightCurrentTrackIn(trackTableView);
        playlistController.setPlaybackService(playbackService);

        TrackDAO    trackDAO    = new JsonTrackDAO();
        PlaylistDAO playlistDAO = new JsonPlaylistDAO();
        this.libraryService = new LibraryService(library, trackDAO);
        trackListController.setLibraryService(libraryService);
        trackListController.setPlaybackService(playbackService);
        trackListController.setCommandHistory(commandHistory);
        trackListController.setTrackTableView(trackTableView);

        titleColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getAuthor()));
        genreColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getGenre()));
        yearColumn.setCellValueFactory(
                data -> new SimpleStringProperty(String.valueOf(data.getValue().getYear())));
        durationColumn.setCellValueFactory(data -> {
            int s = data.getValue().getDuration();
            return new SimpleStringProperty(String.format("%d:%02d", s / 60, s % 60));
        });
        // US-15: colonna con i tag cliccabili accanto alla traccia (slot fissi).
        tagColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()
                        ? null
                        : tagRow(getTableView().getItems().get(getIndex())));
            }
        });

        TableColumn<Track, Void> playTrackColumn = new TableColumn<>("");
        playTrackColumn.setMinWidth(36);
        playTrackColumn.setPrefWidth(36);
        playTrackColumn.setMaxWidth(36);
        playTrackColumn.setResizable(false);
        playTrackColumn.setReorderable(false);
        playTrackColumn.setSortable(false);
        playTrackColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("▶");
            {
                btn.getStyleClass().add("btn-play-inline");
                btn.setOnAction(e -> playbackService.play(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        trackTableView.getColumns().add(0, playTrackColumn);
        trackTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        trackTableView.setItems(libraryService.getAllTracks());

        this.playlistService = new PlaylistService(library, playlistDAO);
        playlistController.setPlaylistService(playlistService);
        playlistController.setLibraryService(libraryService);
        trackListController.setPlaylistService(playlistService);
        playlistController.setCommandHistory(commandHistory);
        playlistController.setLibraryTableView(trackTableView);
        playlistController.setPlaylistFlowPane(playlistFlowPane);
        playlistController.setPlaylistDetail(
                playlistDetailPane, playlistDetailLabel, playlistTrackListView);

        // US-13: Home page sovrapposta alla vista principale nello StackPane.
        // Parte nascosta; l'utente la apre col pulsante "Home" e torna indietro
        // con la freccia "↩", la cui logica di navigazione resta qui.
        homeController.setDependencies(library, libraryService, playbackService);
        homeController.setOnBackToLibrary(this::showLibraryView);

        // US-18b: i service caricano lo stato persistente tramite i rispettivi DAO.
        // LibraryService.load() deve precedere PlaylistService.load() perché il secondo
        // risolve i riferimenti alle tracce embedded contro le istanze già in libreria.
        libraryService.load();
        playlistService.load();

        showLibraryView();
    }

    /**
     * Delega il salvataggio su disco a {@link LibraryService} e {@link PlaylistService}
     * tramite i rispettivi DAO. Invocato dall'evento di chiusura della Stage (US-18a).
     */
    public void saveAll() {
        libraryService.save();
        playlistService.save();
    }

    /**
     * Mostra la Home page (US-13) al posto della vista principale, dopo averne
     * ricalcolato le liste delle tracce e playlist più riprodotte.
     */
    @FXML
    private void onShowHome() {
        homeController.refresh();
        setViewVisible(home, true);
        setViewVisible(mainView, false);
    }

    /**
     * Torna alla vista principale (libreria e playlist) nascondendo la Home.
     * Rinfresca la tabella delle tracce per riflettere eventuali tag (US-15)
     * modificati dalla Home: la TableView non rileva le mutazioni interne agli
     * oggetti Track, quindi senza questo refresh i badge resterebbero obsoleti.
     * È il duale di {@link #onShowHome()}, che rinfresca la Home all'ingresso,
     * così che i tag risultino sempre allineati in entrambe le direzioni.
     */
    private void showLibraryView() {
        setViewVisible(mainView, true);
        setViewVisible(home, false);
        trackTableView.refresh();
    }

    /** Mostra o nasconde una vista dello StackPane, escludendola anche dal layout. */
    private void setViewVisible(Region view, boolean visible) {
        view.setVisible(visible);
        view.setManaged(visible);
    }

    /** Delega l'aggiunta di una traccia a {@link TrackListController}. */
    @FXML
    private void onAddTrackButton() {
        trackListController.onAddTrack();
    }

    /** Delega l'eliminazione di una traccia a {@link TrackListController}. */
    @FXML
    private void onDeleteTrackButton() {
        trackListController.onDeleteTrack();
    }

    /** Delega la modifica di una traccia a {@link TrackListController}. */
    @FXML
    private void onEditTrackButton() {
        trackListController.onEditTrack();
        // US-02: la ListView del dettaglio playlist non riceve notifiche di modifica
        // interna agli oggetti — ObservableList notifica solo add/remove.
        playlistTrackListView.refresh();
    }

    /** Delega la creazione di una playlist a {@link PlaylistController}. */
    @FXML
    private void onCreatePlaylistButton() {
        playlistController.onCreatePlaylist();
    }

    /** Delega l'eliminazione di una playlist a {@link PlaylistController}. */
    @FXML
    private void onDeletePlaylistButton() {
        playlistController.onDeletePlaylist();
    }

    /** Delega la generazione automatica di una playlist per genere a {@link PlaylistController} (US-14a). */
    @FXML
    private void onGenerateByGenreButton() {
        playlistController.onGenerateByGenre();
    }

    /** Delega la generazione automatica di una playlist per anno a {@link PlaylistController} (US-14b). */
    @FXML
    private void onGenerateByYearButton() {
        playlistController.onGenerateByYear();
    }

    /** Delega la generazione automatica di una playlist per tag a {@link PlaylistController} (US-16). */
    @FXML
    private void onGenerateByTagButton() {
        playlistController.onGenerateByTag();
    }

    /** Delega l'aggiunta della traccia selezionata alla playlist a {@link PlaylistController}. */
    @FXML
    private void onAddToPlaylistButton() {
        playlistController.onAddToPlaylist();
    }

    /** Delega la rimozione della traccia selezionata dalla playlist a {@link PlaylistController}. */
    @FXML
    private void onRemoveFromPlaylistButton() {
        playlistController.onRemoveFromPlaylist();
    }

    /** Sposta verso l'alto la traccia selezionata nella playlist. */
    @FXML
    private void onMoveUpButton() {
        playlistController.onMoveUp();
    }

    /** Sposta verso il basso la traccia selezionata nella playlist. */
    @FXML
    private void onMoveDownButton() {
        playlistController.onMoveDown();
    }

    /** Annulla l'ultima operazione eseguita (US-12). */
    @FXML
    private void onUndoButton() {
        commandHistory.undo();
    }

    /**
     * Costruisce gli indicatori dei tag visivi della traccia (US-15): tre slot
     * fissi (preferito, esplicito, nuova uscita), colorati se il tag è attivo e
     * grigi altrimenti. Ogni indicatore è cliccabile e attiva/disattiva il tag
     * direttamente dalla riga della traccia.
     *
     * @param track traccia di cui mostrare i tag
     * @return contenitore con i tre indicatori cliccabili
     */
    private HBox tagRow(Track track) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        for (Tag tag : Tag.values()) {
            row.getChildren().add(tagBadge(track, tag));
        }
        return row;
    }

    /**
     * Crea l'indicatore cliccabile di un tag: al click lo attiva o disattiva
     * tramite {@link LibraryService} e aggiorna la tabella.
     *
     * @param track traccia su cui agire
     * @param tag   tag rappresentato dall'indicatore
     * @return etichetta cliccabile del tag
     */
    private Label tagBadge(Track track, Tag tag) {
        boolean active = track.getTags().contains(tag);
        String text = switch (tag) {
            case FAVOURITE   -> active ? "♥" : "♡";
            case EXPLICIT    -> "E";
            case NEW_RELEASE -> "New";
        };
        String activeStyle = switch (tag) {
            case FAVOURITE   -> "tag-badge-fav";
            case EXPLICIT    -> "tag-badge-explicit";
            case NEW_RELEASE -> "tag-badge-new";
        };
        Label badge = new Label(text);
        badge.getStyleClass().addAll("tag-badge", active ? activeStyle : "tag-badge-off");
        badge.setMinWidth(Region.USE_PREF_SIZE); // mostra sempre il simbolo per intero, mai "..."
        badge.setOnMouseClicked(e -> {
            e.consume();
            if (active) libraryService.removeTag(track, tag);
            else        libraryService.addTag(track, tag);
            trackTableView.refresh();
        });
        return badge;
    }
}