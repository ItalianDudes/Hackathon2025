package it.italiandudes.hackathon2025.javafx.controllers.panel.popup;

import it.italiandudes.hackathon2025.data.DataType;
import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Sensor;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.UIElementConfigurator;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class ControllerScenePanelMainPopupAddSensor {

    // Configuration
    private Sector sector = null;
    private volatile boolean configurationComplete = false;
    public void setSector(@NotNull final Sector sector) {
        this.sector = sector;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Attributes
    private Sensor sensor = null;

    // Methods
    public boolean isSensorAdded() {
        return sensor != null;
    }

    // Graphic Elements
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldIP;
    @FXML private Spinner<Integer> spinnerPort;
    @FXML private ComboBox<DataType> comboBoxDataType;

    // Initialize
    @FXML
    private void initialize() {
        comboBoxDataType.getItems().addAll(DataType.values());
        spinnerPort.getEditor().setTextFormatter(UIElementConfigurator.configureNewIntegerTextFormatter());
        spinnerPort.setPromptText("Porta");
        spinnerPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, 1000, 1));
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
        });
    }

    // EDT
    @FXML
    private void backToPanel() {
        textFieldName.getScene().getWindow().hide();
    }
    @FXML
    private void saveSensor() {
        String name = textFieldName.getText();
        if (name == null || name.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Nome\" e' obbligatorio.");
            return;
        }
        String ip = textFieldIP.getText();
        if (ip == null || ip.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"IP\" e' obbligatorio.");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(spinnerPort.getEditor().getText());
            if (port <= 0 || port >= 65535) {
                new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "La porta deve essere inclusa tra 0 e 65535.");
                return;
            }
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Porta\" e' obbligatorio.");
            return;
        }
        DataType outputType = comboBoxDataType.getSelectionModel().getSelectedItem();
        if (outputType == null) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Tipo di Dato\" e' obbligatorio.");
            return;
        }
        JFXUtils.startVoidServiceTask(() -> {
            try {
                sensor = new Sensor(sector, name, ip, port, outputType);
                Platform.runLater(() -> {
                    new InformationAlert(Client.getStage(),"SUCCESSO", "Salvataggio Completato", "Salvataggio sensore avvenuto con successo!");
                    backToPanel();
                });
            } catch (SQLException e) {
                Platform.runLater(this::backToPanel);
                Client.showMessageAndGoToMenu(e);
            }
        });
    }
}
