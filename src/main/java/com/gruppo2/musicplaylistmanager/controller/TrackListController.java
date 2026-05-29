package com.gruppo2.musicplaylistmanager.controller;

import com.gruppo2.musicplaylistmanager.model.Track;
import com.gruppo2.musicplaylistmanager.service.LibraryService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller che gestisce le operazioni su tracce e playlist.
 */
public class TrackListController {

    private LibraryService libraryService;

    /** Imposta il servizio della libreria usato dal controller. */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Apre il form e, alla conferma, crea e aggiunge una nuova traccia.
     * Se i dati non sono validi mostra un errore e la libreria resta invariata.
     */
    @FXML
    public void onAddTrack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gruppo2/musicplaylistmanager/view/TrackForm.fxml"));
            GridPane form = loader.load();

            // I campi del form si recuperano tramite il loro fx:id.
            TextField titleField = (TextField) loader.getNamespace().get("titleField");
            TextField authorField = (TextField) loader.getNamespace().get("authorField");
            TextField durationField = (TextField) loader.getNamespace().get("durationField");
            TextField genreField = (TextField) loader.getNamespace().get("genreField");
            TextField yearField = (TextField) loader.getNamespace().get("yearField");

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Aggiungi traccia");
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int duration = Integer.parseInt(durationField.getText().trim());
                int year = Integer.parseInt(yearField.getText().trim());
                Track track = new Track(
                        titleField.getText().trim(),
                        authorField.getText().trim(),
                        duration,
                        genreField.getText().trim(),
                        year);
                libraryService.addTrack(track);
            }
        } catch (NumberFormatException e) {
            showError("Durata e anno devono essere numeri interi.");
        } catch (IllegalArgumentException e) {
            // Errore di validazione proveniente dal service.
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Impossibile caricare il form di inserimento.");
        }
    }

    /** Mostra una finestra di errore con il messaggio indicato. */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //  Metodi a carico di altri membri 

    @FXML
    public void onDeleteTrack() {
        
    }

    @FXML
    public void onEditTrack() {
    
    }
}