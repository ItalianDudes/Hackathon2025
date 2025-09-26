package it.italiandudes.hackathon2025.javafx.controllers.panel.popup;

import it.italiandudes.hackathon2025.data.Actuator;
import it.italiandudes.hackathon2025.data.DataType;
import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.UIElementConfigurator;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class ControllerScenePanelMainPopupAddActuator {

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
    private Actuator actuator = null;

    // Methods
    public boolean isActuatorAdded() {
        return actuator != null;
    }

    // Graphic Elements
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldIP;
    @FXML private Spinner<Integer> spinnerPort;
    @FXML private TextField textFieldMinValue;
    @FXML private TextField textFieldValue;
    @FXML private TextField textFieldMaxValue;
    @FXML private ComboBox<DataType> comboBoxDataType;
    @FXML private CheckBox checkBoxAllowOverride;

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
    private void saveActuator() {
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

        boolean allowOverride = checkBoxAllowOverride.isSelected();

        DataType inputType = comboBoxDataType.getSelectionModel().getSelectedItem();
        if (inputType == null) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Tipo di Dato\" e' obbligatorio.");
            return;
        }
        String minValueText = textFieldMinValue.getText();
        if (minValueText == null || minValueText.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Minimo\" e' obbligatorio.");
            return;
        }
        String valueText = textFieldValue.getText();
        if (valueText == null || valueText.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Attuale\" e' obbligatorio.");
            return;
        }
        String maxValueText = textFieldMaxValue.getText();
        if (maxValueText == null || maxValueText.isBlank()) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Massimo\" e' obbligatorio.");
            return;
        }

        if (inputType == DataType.INTEGER) {
            try {
                int minValue = Integer.parseInt(minValueText);
                int value = Integer.parseInt(valueText);
                int maxValue = Integer.parseInt(maxValueText);

                if (minValue >= maxValue) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Minimo\" deve contenere un numero intero valido minore di \"Valore Massimo\".");
                    return;
                } else if (value < minValue || value > maxValue) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Attuale\" deve contenere un numero intero valido maggiore o uguale a \"Valore Minimo\" e minore o uguale a \"Valore Massimo\".");
                    return;
                }

                JFXUtils.startVoidServiceTask(() -> {
                    try {
                        actuator = new Actuator(sector, name, ip, port, value, minValue, maxValue, inputType, allowOverride);
                        Platform.runLater(() -> {
                            new InformationAlert(Client.getStage(),"SUCCESSO", "Salvataggio Completato", "Salvataggio sensore avvenuto con successo!");
                            backToPanel();
                        });
                    } catch (SQLException e) {
                        Platform.runLater(this::backToPanel);
                        Client.showMessageAndGoToMenu(e);
                    }
                });
            } catch (NumberFormatException e) {
                new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "I campi valore devono contenere numeri interi validi.");
            }
        } else if (inputType == DataType.DOUBLE) {
            try {
                double minValue = Double.parseDouble(minValueText);
                double value = Double.parseDouble(valueText);
                double maxValue = Double.parseDouble(maxValueText);

                if (minValue >= maxValue) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Minimo\" deve contenere un numero decimale valido minore di \"Valore Massimo\".");
                    return;
                } else if (value < minValue || value > maxValue) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il campo \"Valore Attuale\" deve contenere un numero decimale valido maggiore o uguale a \"Valore Minimo\" e minore o uguale a \"Valore Massimo\".");
                    return;
                }

                JFXUtils.startVoidServiceTask(() -> {
                    try {
                        actuator = new Actuator(sector, name, ip, port, value, minValue, maxValue, inputType, allowOverride);
                        Platform.runLater(() -> {
                            new InformationAlert(Client.getStage(),"SUCCESSO", "Salvataggio Completato", "Salvataggio sensore avvenuto con successo!");
                            backToPanel();
                        });
                    } catch (SQLException e) {
                        Platform.runLater(this::backToPanel);
                        Client.showMessageAndGoToMenu(e);
                    }
                });
            } catch (NumberFormatException e) {
                new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "I campi valore devono contenere numeri interi validi.");
            }
        }
    }

}
