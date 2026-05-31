package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.command.AddTrackToLibraryCommand;
import com.gruppo2.musicplaylistmanager.command.CommandHistory;
import com.gruppo2.musicplaylistmanager.command.RemoveTrackFromLibraryCommand;
import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller che gestisce le operazioni sulle tracce: aggiunta, eliminazione
 * e modifica. Riceve il riferimento alla {@link TableView}, al
 * {@link LibraryService} e alla {@link CommandHistory} condivisa da
 * {@link MainController} tramite i rispettivi setter.
 * <p>
 * Tutte le operazioni di scrittura vengono incapsulate in un oggetto
 * {@link com.gruppo2.musicplaylistmanager.command.Command} e affidate alla
 * {@code CommandHistory}, in modo che l'undo possa essere abilitato in US-12
 * senza modificare questo controller.
 * </p>
 */
public class TrackListController {

    private LibraryService   libraryService;
    private CommandHistory   commandHistory;
    private TableView<Track> trackTableView;

    /**
     * Inietta il servizio della libreria.
     *
     * @param libraryService servizio condiviso con gli altri controller
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Inietta la {@link CommandHistory} condivisa, usata per registrare
     * le operazioni di aggiunta, modifica e rimozione di tracce.
     *
     * @param commandHistory storia dei comandi condivisa con gli altri controller
     */
    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * Inietta la TableView su cui operare per leggere la selezione corrente.
     *
     * @param trackTableView tabella delle tracce della libreria
     */
    public void setTrackTableView(TableView<Track> trackTableView) {
        this.trackTableView = trackTableView;
    }

    /**
     * Apre il form di inserimento e, alla conferma, crea e aggiunge una nuova
     * traccia alla libreria tramite {@link AddTrackToLibraryCommand}.
     * Se i dati non superano la validazione mostra un alert di errore e la
     * libreria resta invariata.
     */
    @FXML
    public void onAddTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gruppo2/musicplaylistmanager/view/TrackForm.fxml"));
            GridPane form = loader.load();

            TextField titleField    = (TextField) loader.getNamespace().get("titleField");
            TextField authorField   = (TextField) loader.getNamespace().get("authorField");
            TextField durationField = (TextField) loader.getNamespace().get("durationField");
            TextField genreField    = (TextField) loader.getNamespace().get("genreField");
            TextField yearField     = (TextField) loader.getNamespace().get("yearField");

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Aggiungi traccia");
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int duration = Integer.parseInt(durationField.getText().trim());
                int year     = Integer.parseInt(yearField.getText().trim());
                Track track  = new Track(
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        duration,
                        genreField.getText().trim(),
                        year);
                commandHistory.push(new AddTrackToLibraryCommand(libraryService, track));
            }
        } catch (NumberFormatException e) {
            showError("Durata e anno devono essere numeri interi.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Impossibile caricare il form di inserimento.");
        }
    }

    /**
     * Elimina la traccia selezionata dalla libreria e da tutte le playlist che
     * la contengono, previa conferma dell'utente tramite dialog.
     * L'operazione viene incapsulata in un {@link RemoveTrackFromLibraryCommand}.
     * Se nessuna traccia è selezionata mostra un alert di errore.
     */
    @FXML
    public void onDeleteTrack() {
        Track selected = trackTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una traccia da eliminare.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Eliminare la traccia \"" + selected.getTitle() + "\"?\n" +
                "Verrà rimossa anche da tutte le playlist che la contengono.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            commandHistory.push(new RemoveTrackFromLibraryCommand(libraryService, selected));
        }
    }

    /**
     * Apre il form di modifica pre-compilato con i dati della traccia selezionata.
     * Alla conferma aggiorna i campi tramite {@link LibraryService#updateTrack}.
     * Il metodo chiama {@link TableView#refresh()} perché la TableView non rileva
     * automaticamente le modifiche ai campi interni degli oggetti già presenti.
     */
    @FXML
    public void onEditTrack() {
        Track selected = trackTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una traccia da modificare.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gruppo2/musicplaylistmanager/view/TrackForm.fxml"));
            GridPane form = loader.load();

            TextField titleField    = (TextField) loader.getNamespace().get("titleField");
            TextField authorField   = (TextField) loader.getNamespace().get("authorField");
            TextField durationField = (TextField) loader.getNamespace().get("durationField");
            TextField genreField    = (TextField) loader.getNamespace().get("genreField");
            TextField yearField     = (TextField) loader.getNamespace().get("yearField");

            titleField.setText(selected.getTitle());
            authorField.setText(selected.getAuthor());
            durationField.setText(String.valueOf(selected.getDuration()));
            genreField.setText(selected.getGenre());
            yearField.setText(String.valueOf(selected.getYear()));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Modifica traccia");
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int duration = Integer.parseInt(durationField.getText().trim());
                int year     = Integer.parseInt(yearField.getText().trim());
                libraryService.updateTrack(
                        selected,
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        duration,
                        genreField.getText().trim(),
                        year);
                trackTableView.refresh();
            }
        } catch (NumberFormatException e) {
            showError("Durata e anno devono essere numeri interi.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Impossibile caricare il form di modifica.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
