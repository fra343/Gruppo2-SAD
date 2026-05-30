package com.gruppo2.musicplaylistmanager.controller;

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


//Controller che gestisce le operazioni sulle tracce: aggiunta, rimozione, modifica.
//Riceve il riferimento alla TableView da MainController tramite setTrackTableView.

public class TrackListController {

    private LibraryService libraryService;

    // Riferimento alla TableView della libreria, iniettato da MainController.
    private TableView<Track> trackTableView;

    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    //Imposta la TableView su cui operare per la selezione delle tracce.
    public void setTrackTableView(TableView<Track> trackTableView) {
        this.trackTableView = trackTableView;
    }

    
    //Apre il form e, alla conferma, crea e aggiunge una nuova traccia.
    //Se i dati non sono validi mostra un errore e la libreria resta invariata.

    @FXML
    public void onAddTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gruppo2/musicplaylistmanager/view/TrackForm.fxml"));
            GridPane form = loader.load();

            // I campi del form si recuperano tramite il loro fx:id
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
                libraryService.addTrack(track);
                // La TableView si aggiorna da sola grazie all'ObservableList
            }
        } catch (NumberFormatException e) {
            showError("Durata e anno devono essere numeri interi.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Impossibile caricare il form di inserimento.");
        }
    }

    @FXML
    public void onDeleteTrack() {
    }

    //Modifica traccia: apre il form pre-compilato con i dati della traccia selezionata.
    //Alla conferma aggiorna i campi tramite LibraryService.updateTrack.

    @FXML
    public void onEditTrack() {
        Track selected = trackTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleziona una traccia da modificare.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gruppo2/musicplaylistmanager/view/TrackForm.fxml"));
            GridPane form = loader.load();

            TextField titleField    = (TextField) loader.getNamespace().get("titleField");
            TextField authorField   = (TextField) loader.getNamespace().get("authorField");
            TextField durationField = (TextField) loader.getNamespace().get("durationField");
            TextField genreField    = (TextField) loader.getNamespace().get("genreField");
            TextField yearField     = (TextField) loader.getNamespace().get("yearField");

            // Pre-popola i campi con i dati attuali della traccia
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
                // La TableView non rileva automaticamente le modifiche ai campi interni:
                // refresh() forza il ridisegno di tutte le righe visibili.
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
