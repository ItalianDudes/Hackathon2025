package it.italiandudes.hackathon2025.javafx.controllers.panel.popup;

import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Terrain;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class ControllerScenePanelMainPopupAddSector {

    // Configuration
    private Terrain terrain = null;
    private volatile boolean configurationComplete = false;
    public void setTerrain(@NotNull final Terrain terrain) {
        this.terrain = terrain;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Attributes
    private Sector sector = null;

    // Methods
    public boolean isSectorAdded() {
        return sector != null;
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
    private void saveSector() {
        String name = textFieldName.getText();
        if (name == null || name.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Nome\" e' obbligatorio.");
            return;
        }
        String dimension = textFieldDimension.getText();
        JFXUtils.startVoidServiceTask(() -> {
            try {
                sector = new Sector(terrain, name, dimension.isBlank() ? null : dimension);
                Platform.runLater(() -> {
                    new InformationAlert(Client.getStage(),"SUCCESSO", "Salvataggio Completato", "Salvataggio settore avvenuto con successo!");
                    backToMainPanel();
                });
            } catch (SQLException e) {
                Platform.runLater(this::backToMainPanel);
                Client.showMessageAndGoToMenu(e);
            }
        });
    }

}
