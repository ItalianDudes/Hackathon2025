package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Sensor;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.hackathon2025.javafx.controllers.panel.popup.ControllerScenePanelMainPopupAddSensor;
import it.italiandudes.hackathon2025.javafx.scene.panel.popup.ScenePanelMainPopupAddSensor;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.components.SceneController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class ControllerScenePanelTabSensors extends ConfigurableTabController {

    // Attributes
    @NotNull private final ArrayList<@NotNull Sensor> sensors = new ArrayList<>();

    // Graphic Elements
    @FXML private ListView<Sensor> listViewSensors;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
            reloadSensorListView();
        });
    }

    // EDT
    @FXML
    private void showSensorsContextMenu(@NotNull ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addSensor = new MenuItem("Aggiungi Sensore");
        addSensor.setOnAction(actionEvent -> {
            Sector selectedSector = super.getMainController().getSelectedSector();
            if (selectedSector == null) return;
            SceneController popupSceneController = ScenePanelMainPopupAddSensor.getScene(selectedSector);
            ControllerScenePanelMainPopupAddSensor controller = (ControllerScenePanelMainPopupAddSensor) popupSceneController.getController();
            Stage popupStage = Client.initPopupStage(popupSceneController);
            popupStage.setTitle("Aggiungi Sensore");
            popupStage.showAndWait();
            if (controller.isSensorAdded()) {
                reloadSensorListView();
            }
        });
        contextMenu.getItems().add(addSensor);

        Sensor selectedSensor = listViewSensors.getSelectionModel().getSelectedItem();
        if (selectedSensor != null) {
            MenuItem removeSensor = new MenuItem("Rimuovi Sensore");
            removeSensor.setOnAction(actionEvent -> JFXUtils.startVoidServiceTask(() -> {
                try {
                    selectedSensor.delete();
                    reloadSensorListView();
                } catch (SQLException e) {
                    Client.showMessageAndGoToMenu(e);
                }
            }));
            contextMenu.getItems().add(removeSensor);
        }

        MenuItem clearSelection = new MenuItem("Svuota Selezione");
        clearSelection.setOnAction(actionEvent -> listViewSensors.getSelectionModel().clearSelection());
        contextMenu.getItems().add(clearSelection);

        contextMenu.setAutoHide(true);
        contextMenu.show(Client.getStage(), event.getScreenX(), event.getScreenY());
    }
    private void reloadSensorListView() {
        JFXUtils.startVoidServiceTask(() -> {
            try {
                loadSensorsFromDB();
                Platform.runLater(() -> {
                    listViewSensors.getItems().clear();
                    listViewSensors.getItems().setAll(sensors);
                });
            } catch (SQLException e) {
                Client.showMessageAndGoToMenu(e);
            }
        });
    }
    private void loadSensorsFromDB() throws SQLException {
        sensors.clear();
        String query = "SELECT id FROM sensors;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            sensors.add(new Sensor(result.getLong("id")));
        }
        ps.close();
    }

}
