package it.italiandudes.hackathon2025.javafx.components;

import it.italiandudes.hackathon2025.data.Actuator;
import it.italiandudes.hackathon2025.data.DataType;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Getter
public class ComponentActuator extends GridPane {

    // Attributes
    @NotNull private final Actuator actuator;

    // Graphic Elements
    private final Label labelName;
    private final Label labelValue;
    private final CheckBox checkBoxEnableOverride;
    private final TextField textFieldValue;
    private final Button buttonSendValue;

    // Constructor
    public ComponentActuator(@NotNull final Actuator actuator) {
        this.actuator = actuator;
        labelName = new Label(actuator.getName());
        labelName.setMinWidth(Label.USE_PREF_SIZE);
        labelName.setPrefWidth(100);
        labelName.setMaxWidth(Double.MAX_VALUE);
        labelName.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;");

        labelValue = new Label(actuator.getValue().toString());
        labelValue.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;");
        checkBoxEnableOverride = new CheckBox("Attiva Override");
        checkBoxEnableOverride.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;");
        textFieldValue = new TextField();
        textFieldValue.setStyle("-fx-font-size: 24px;");
        textFieldValue.setPromptText("Nuovo Valore");
        buttonSendValue = new Button("Applica");
        buttonSendValue.setOnAction(actionEvent -> {
            String value = textFieldValue.getText();
            if (value == null || value.isBlank()) {
                new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "E' obbligatorio inserire un nuovo valore.");
                return;
            }
            if (actuator.getInputType() == DataType.INTEGER) {
                try {
                    int newValue = Integer.parseInt(value);
                    if (newValue < actuator.getMinValue().intValue() || newValue > actuator.getMaxValue().intValue()) {
                        new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il nuovo valore deve essere compreso tra " + actuator.getMinValue().intValue() + " e " + actuator.getMaxValue().intValue() + ".");
                        return;
                    }
                    labelValue.setText(String.valueOf(newValue));
                    new InformationAlert(Client.getStage(), "SUCCESSO", "Applicazione Modifica", "Modifica applicata con successo!");
                    textFieldValue.setText(null);
                } catch (NumberFormatException e) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il nuovo valore deve essere un numero intero valido.");
                }
            } else if (actuator.getInputType() == DataType.DOUBLE) {
                try {
                    double newValue = Integer.parseInt(value);
                    if (newValue < actuator.getMinValue().doubleValue() || newValue > actuator.getMaxValue().doubleValue()) {
                        new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il nuovo valore deve essere compreso tra " + actuator.getMinValue().doubleValue() + " e " + actuator.getMaxValue().doubleValue() + ".");
                        return;
                    }
                    labelValue.setText(String.valueOf(newValue));
                    new InformationAlert(Client.getStage(), "SUCCESSO", "Applicazione Modifica", "Modifica applicata con successo!");
                    textFieldValue.setText(null);
                } catch (NumberFormatException e) {
                    new ErrorAlert(Client.getStage(), "ERRORE", "Errore di Inserimento", "Il nuovo valore deve essere un numero decimale valido.");
                }
            }
        });
        buttonSendValue.setStyle("-fx-font-size: 24px;");
        if (!actuator.isAllowOverride()) {
            checkBoxEnableOverride.setDisable(true);
        }
        textFieldValue.setDisable(true);
        buttonSendValue.setDisable(true);

        checkBoxEnableOverride.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                JFXUtils.startVoidServiceTask(() -> {
                    try {
                        actuator.setOverrideStatus(true);
                    } catch (SQLException e) {
                        Client.showMessageAndGoToMenu(e);
                    }
                });
                textFieldValue.setDisable(false);
                buttonSendValue.setDisable(false);
            } else {
                JFXUtils.startVoidServiceTask(() -> {
                    try {
                        actuator.setOverrideStatus(false);
                    } catch (SQLException e) {
                        Client.showMessageAndGoToMenu(e);
                    }
                });
                textFieldValue.setDisable(true);
                buttonSendValue.setDisable(true);
            }
        });
        checkBoxEnableOverride.setSelected(actuator.isOverrideActive());

        setHgap(5);
        RowConstraints row = new RowConstraints();
        row.setMinHeight(GridPane.USE_COMPUTED_SIZE);
        row.setPrefHeight(GridPane.USE_COMPUTED_SIZE);
        row.setMaxHeight(GridPane.USE_COMPUTED_SIZE);
        getRowConstraints().add(row);
        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(GridPane.USE_COMPUTED_SIZE);
            col.setPrefWidth(GridPane.USE_COMPUTED_SIZE);
            col.setMaxWidth(Double.MAX_VALUE);
            if (i <= 1) {
                col.setHgrow(Priority.ALWAYS);
                col.setHalignment(HPos.LEFT);
            } else {
                col.setHgrow(Priority.NEVER);
                col.setHalignment(HPos.CENTER);
            }
            getColumnConstraints().add(col);
        }
        add(labelName, 0, 0);
        add(labelValue, 1, 0);
        add(checkBoxEnableOverride, 2, 0);
        add(textFieldValue, 3, 0);
        add(buttonSendValue, 4, 0);
    }

    // ToString
    @Override
    public String toString() {
        return actuator.getName();
    }
}
