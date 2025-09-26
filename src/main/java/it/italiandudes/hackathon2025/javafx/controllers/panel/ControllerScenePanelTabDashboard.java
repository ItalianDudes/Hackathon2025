package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.Actuator;
import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Sensor;
import it.italiandudes.hackathon2025.data.Terrain;
import it.italiandudes.hackathon2025.data.generator.DataGenerator;
import it.italiandudes.hackathon2025.data.generator.DoubleDataGenerator;
import it.italiandudes.hackathon2025.data.generator.IntegerDataGenerator;
import it.italiandudes.hackathon2025.data.warning.Warning;
import it.italiandudes.hackathon2025.data.warning.WarningOrigin;
import it.italiandudes.hackathon2025.data.warning.WarningSeverity;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.logger.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public final class ControllerScenePanelTabDashboard extends ConfigurableTabController {

    // Graphic Elements
    @FXML private Label labelTerrainID;
    @FXML private Label labelTerrainName;
    @FXML private Label labelOwner;
    @FXML private Label labelTerrainDimension;
    @FXML private Label labelSectorID;
    @FXML private Label labelSectorName;
    @FXML private Label labelSectorDimension;
    @FXML private Label labelSectorRegisteredSensors;
    @FXML private Label labelSectorRegisteredActuators;
    @FXML private Label labelSectorActiveAlarms;
    @FXML private Label labelSectorActiveOverrides;
    @FXML private ListView<Warning> listViewWarnings;


    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
        });
        JFXUtils.startVoidServiceTask(() -> {
            while (DBManager.isConnectionOpen()) {
                try {
                    Thread.sleep(Duration.of(3, TimeUnit.SECONDS.toChronoUnit()));
                } catch (InterruptedException e) {
                    Logger.log(e);
                }
                Platform.runLater(this::refreshTerrainAndSectorInfo);
            }
        });
        JFXUtils.startVoidServiceTask(() -> {
            while (DBManager.isConnectionOpen()) {
                try {
                    Thread.sleep(Duration.of(3, TimeUnit.SECONDS.toChronoUnit()));
                } catch (InterruptedException e) {
                    Logger.log(e);
                }
                ArrayList<Warning> warnings = new ArrayList<>();
                int activeWarnings = 0;
                for (Sensor sensor : getMainController().getControllerSensors().getSensors()) {
                    DataGenerator dataGenerator = sensor.getDataGenerator();
                    WarningOrigin origin = WarningOrigin.SENSOR;
                    WarningSeverity severity = null;
                    String reason = null;
                    if (dataGenerator instanceof IntegerDataGenerator) {
                        int lastValue = ((IntegerDataGenerator) dataGenerator).getLastGeneratedValue();
                        if (lastValue >= ((IntegerDataGenerator) dataGenerator).getMaxValue()) {
                            reason = "Limite Sensore Superiore Superato: " + lastValue + "/" + ((IntegerDataGenerator) dataGenerator).getMaxValue();
                            severity = WarningSeverity.EMERGENCY;
                        } else if (lastValue <= ((IntegerDataGenerator) dataGenerator).getMinValue()) {
                            reason = "Limite Sensore Inferiore Superato: " + lastValue + "/" + ((IntegerDataGenerator) dataGenerator).getMinValue();
                            severity = WarningSeverity.EMERGENCY;
                        } else if (lastValue >= ((IntegerDataGenerator) dataGenerator).getAlarmMaxValue()) {
                            reason = "Limite di Sicurezza Superiore Superato: " + lastValue + "/" + ((IntegerDataGenerator) dataGenerator).getAlarmMaxValue();
                            severity = WarningSeverity.MAJOR;
                        } else if (lastValue <= ((IntegerDataGenerator) dataGenerator).getAlarmMinValue()) {
                            reason = "Limite di Sicurezza Inferiore Superato: " + lastValue + "/" + ((IntegerDataGenerator) dataGenerator).getAlarmMinValue();
                            severity = WarningSeverity.MAJOR;
                        }
                    } else if (dataGenerator instanceof DoubleDataGenerator) {
                        double lastValue = ((DoubleDataGenerator) dataGenerator).getLastGeneratedValue();
                        if (lastValue >= ((DoubleDataGenerator) dataGenerator).getMaxValue()) {
                            reason = "Limite Sensore Superiore Superato: " + lastValue + "/" + ((DoubleDataGenerator) dataGenerator).getMaxValue();
                            severity = WarningSeverity.CRITICAL;
                        } else if (lastValue <= ((DoubleDataGenerator) dataGenerator).getMinValue()) {
                            reason = "Limite Sensore Inferiore Superato: " + lastValue + "/" + ((DoubleDataGenerator) dataGenerator).getMinValue();
                            severity = WarningSeverity.CRITICAL;
                        } else if (lastValue >= ((DoubleDataGenerator) dataGenerator).getAlarmMaxValue()) {
                            reason = "Limite di Sicurezza Superiore Superato: " + lastValue + "/" + ((DoubleDataGenerator) dataGenerator).getAlarmMaxValue();
                            severity = WarningSeverity.MAJOR;
                        } else if (lastValue <= ((DoubleDataGenerator) dataGenerator).getAlarmMinValue()) {
                            reason = "Limite di Sicurezza Inferiore Superato: " + lastValue + "/" + ((DoubleDataGenerator) dataGenerator).getAlarmMinValue();
                            severity = WarningSeverity.MAJOR;
                        }
                    }
                    if (reason != null) {
                        activeWarnings++;
                        warnings.add(new Warning(origin, severity, reason));
                    }
                }
                int activeOverrides = 0;
                for (Actuator actuator : getMainController().getControllerActuators().getActuators()) {
                    final WarningOrigin origin = WarningOrigin.ACTUATOR_OVERRIDE;
                    final String reason = "Attuatore \"" + actuator.getName() + "\" in stato di Override!";
                    if (actuator.isOverrideActive()) {
                        activeOverrides++;
                        warnings.add(new Warning(origin, WarningSeverity.INFO, reason));
                    }
                }
                int finalActiveWarnings = activeWarnings;
                int finalActiveOverrides = activeOverrides;
                Platform.runLater(() -> {
                    labelSectorActiveAlarms.setText(String.valueOf(finalActiveWarnings));
                    labelSectorActiveOverrides.setText(String.valueOf(finalActiveOverrides));
                    listViewWarnings.getItems().setAll(warnings);
                });
            }
        });
    }

    // Methods
    public void refreshTerrainAndSectorInfo() {
        Terrain terrain = super.getMainController().getSelectedTerrain();
        Sector sector = super.getMainController().getSelectedSector();
        if (terrain == null || sector == null) return;
        // labelTerrainID.setText("TERRENO#" + terrain.getUuid());
        labelTerrainName.setText(terrain.getName());
        labelOwner.setText(terrain.getOwner());
        labelTerrainDimension.setText(terrain.getDimension());
        // labelSectorID.setText("SETTORE#" + sector.getUuid());
        labelSectorName.setText(sector.getName());
        labelSectorDimension.setText(sector.getDimension());
        labelSectorRegisteredSensors.setText(String.valueOf(super.getMainController().getControllerSensors().getSensors().size()));
        labelSectorRegisteredActuators.setText(String.valueOf(super.getMainController().getControllerActuators().getActuators().size()));
    }
}
