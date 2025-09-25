package it.italiandudes.hackathon2025.javafx.controllers.panel.popup;

import it.italiandudes.hackathon2025.data.Terrain;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public final class ControllerScenePanelMainPopupAddTerrain {

    // Configuration
    private volatile boolean configurationComplete = false;
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Attributes
    private Terrain terrain = null;

    // Methods
    public boolean isTerrainAdded() {
        return terrain != null;
    }

    // Graphic Elements
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldOwner;
    @FXML private TextField textFieldDimension;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
        });
    }

    // EDT
    @FXML
    private void backToMainPanel() {
        textFieldName.getScene().getWindow().hide();
    }
    @FXML
    private void saveTerrain() {
        String name = textFieldName.getText();
        if (name == null || name.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Nome\" e' obbligatorio.");
            return;
        }
        String owner = textFieldOwner.getText();
        if (owner == null || owner.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Proprietario\" e' obbligatorio.");
            return;
        }
        String dimension = textFieldDimension.getText();
        if (dimension == null || dimension.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Dimensione\" e' obbligatorio.");
            return;
        }
        JFXUtils.startVoidServiceTask(() -> {
            try {
                terrain = new Terrain(name, owner, dimension);
                Platform.runLater(() -> {
                    new InformationAlert(Client.getStage(),"SUCCESSO", "Salvataggio Completato", "Salvataggio terreno avvenuto con successo!");
                    backToMainPanel();
                });
            } catch (SQLException e) {
                Platform.runLater(this::backToMainPanel);
                Client.showMessageAndGoToMenu(e);
            }
        });
    }

}
