package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.DataType;
import it.italiandudes.hackathon2025.data.Sensor;
import it.italiandudes.hackathon2025.data.generator.DoubleDataGenerator;
import it.italiandudes.hackathon2025.data.generator.IntegerDataGenerator;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.idl.javafx.JFXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class ControllerScenePanelTabGraphs extends ConfigurableTabController {

    // Attributes
    private LineChart<Number, Number> sensor1Chart = null;
    private LineChart<Number, Number> sensor2Chart = null;
    private Thread sensor1GraphDataThread = null;
    private Thread sensor2GraphDataThread = null;

    // Graphic Elements
    @FXML private ComboBox<Sensor> comboBoxSensor1;
    @FXML private ComboBox<Sensor> comboBoxSensor2;
    @FXML private AnchorPane anchorPaneSensorGraph1;
    @FXML private AnchorPane anchorPaneSensorGraph2;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
        });
    }

    // EDT
    @FXML
    private void updateSensorsCombobox() {
        if (sensor1GraphDataThread != null) sensor1GraphDataThread.interrupt();
        if (sensor2GraphDataThread != null) sensor2GraphDataThread.interrupt();
        comboBoxSensor1.getItems().setAll(getMainController().getControllerSensors().getSensors().stream().filter(sensor -> sensor.getDataGenerator() != null).toList());
        comboBoxSensor2.getItems().setAll(getMainController().getControllerSensors().getSensors().stream().filter(sensor -> sensor.getDataGenerator() != null).toList());
    }
    @FXML
    private void changeSensor1() {
        Sensor sensor = comboBoxSensor1.getSelectionModel().getSelectedItem();
        anchorPaneSensorGraph1.getChildren().clear();
        sensor1Chart = null;
        if (sensor1GraphDataThread != null) sensor1GraphDataThread.interrupt();
        if (sensor == null) return;
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("DataSet");
        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(sensor.getUnitOfMeasurement());
        sensor1Chart = new LineChart<>(xAxis, yAxis);
        sensor1Chart.setAnimated(false);
        sensor1Chart.setLegendVisible(false);
        sensor1Chart.getData().clear();
        anchorPaneSensorGraph1.getChildren().add(sensor1Chart);
        AnchorPane.setTopAnchor(sensor1Chart, 0.0);
        AnchorPane.setLeftAnchor(sensor1Chart, 0.0);
        AnchorPane.setBottomAnchor(sensor1Chart, 0.0);
        AnchorPane.setRightAnchor(sensor1Chart, 0.0);
        sensor1GraphDataThread = new Thread(() -> {
            while (DBManager.isConnectionOpen() && !sensor.isSensorDeleted() && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(Duration.of(25, ChronoUnit.MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
                assert sensor.getDataGenerator() != null;
                if (sensor.getOutputType() == DataType.INTEGER) {
                    List<Integer> dataset = ((IntegerDataGenerator) sensor.getDataGenerator()).getCurrentDataset();
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    try {
                        for (int i = 0; i < dataset.size(); i++) {
                            series.getData().add(new XYChart.Data<>(i, dataset.get(i)));
                        }
                        Platform.runLater(() -> {
                            sensor1Chart.getData().clear();
                            sensor1Chart.getData().add(series);
                        });
                    } catch (Exception ignored) {}
                } else if (sensor.getOutputType() == DataType.DOUBLE) {
                    List<Double> dataset = ((DoubleDataGenerator) sensor.getDataGenerator()).getCurrentDataset();
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    try {
                        for (int i = 0; i < dataset.size(); i++) {
                            series.getData().add(new XYChart.Data<>(i, dataset.get(i)));
                        }
                        Platform.runLater(() -> {
                            sensor1Chart.getData().clear();
                            sensor1Chart.getData().add(series);
                        });
                    } catch (Exception ignored) {}
                }
            }
        });
        sensor1GraphDataThread.setName("Sensor 1 Data Thread");
        sensor1GraphDataThread.start();
    }
    @FXML
    private void changeSensor2() {
        Sensor sensor = comboBoxSensor2.getSelectionModel().getSelectedItem();
        anchorPaneSensorGraph2.getChildren().clear();
        sensor2Chart = null;
        if (sensor2GraphDataThread != null) sensor2GraphDataThread.interrupt();
        if (sensor == null) return;
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("DataSet");
        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(sensor.getUnitOfMeasurement());
        sensor2Chart = new LineChart<>(xAxis, yAxis);
        sensor2Chart.setAnimated(false);
        sensor2Chart.setLegendVisible(false);
        sensor2Chart.getData().clear();
        anchorPaneSensorGraph2.getChildren().add(sensor2Chart);
        AnchorPane.setTopAnchor(sensor2Chart, 0.0);
        AnchorPane.setLeftAnchor(sensor2Chart, 0.0);
        AnchorPane.setBottomAnchor(sensor2Chart, 0.0);
        AnchorPane.setRightAnchor(sensor2Chart, 0.0);
        sensor2GraphDataThread = new Thread(() -> {
            while (DBManager.isConnectionOpen() && !sensor.isSensorDeleted() && !Thread.currentThread().isInterrupted()) {
                assert sensor.getDataGenerator() != null;
                try {
                    Thread.sleep(sensor.getDataGenerator().getGenerationInterval().plus(100, ChronoUnit.MILLIS));
                } catch (InterruptedException e) {
                    break;
                }
                if (sensor.getOutputType() == DataType.INTEGER) {
                    List<Integer> dataset = ((IntegerDataGenerator) sensor.getDataGenerator()).getCurrentDataset();
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    for (int i=0; i<dataset.size(); i++) {
                        series.getData().add(new XYChart.Data<>(i, dataset.get(i)));
                    }
                    Platform.runLater(() -> {
                        sensor2Chart.getData().clear();
                        sensor2Chart.getData().add(series);
                    });
                } else if (sensor.getOutputType() == DataType.DOUBLE) {
                    List<Double> dataset = ((DoubleDataGenerator) sensor.getDataGenerator()).getCurrentDataset();
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    for (int i=0; i<dataset.size(); i++) {
                        series.getData().add(new XYChart.Data<>(i, dataset.get(i)));
                    }
                    Platform.runLater(() -> {
                        sensor2Chart.getData().clear();
                        sensor2Chart.getData().add(series);
                    });
                }
            }
        });
        sensor2GraphDataThread.setName("Sensor 2 Data Thread");
        sensor2GraphDataThread.start();
    }
}
