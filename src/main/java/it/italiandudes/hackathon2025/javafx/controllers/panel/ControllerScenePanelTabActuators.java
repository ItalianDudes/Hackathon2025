package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.Actuator;
import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.hackathon2025.javafx.controllers.panel.popup.ControllerScenePanelMainPopupAddActuator;
import it.italiandudes.hackathon2025.javafx.scene.panel.popup.ScenePanelMainPopupAddActuator;
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

public final class ControllerScenePanelTabActuators extends ConfigurableTabController {

    // Attributes
    @NotNull private final ArrayList<@NotNull Actuator> actuators = new ArrayList<>();

    // Graphic Elements
    @FXML private ListView<Actuator> listViewActuators;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
            reloadActuatorListView();
        });
    }

    // EDT
    @FXML
    private void showActuatorsContextMenu(@NotNull ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addActuator = new MenuItem("Aggiungi Attuatore");
        addActuator.setOnAction(actionEvent -> {
            Sector selectedSector = super.getMainController().getSelectedSector();
            if (selectedSector == null) return;
            SceneController popupSceneController = ScenePanelMainPopupAddActuator.getScene(selectedSector);
            ControllerScenePanelMainPopupAddActuator controller = (ControllerScenePanelMainPopupAddActuator) popupSceneController.getController();
            Stage popupStage = Client.initPopupStage(popupSceneController);
            popupStage.setTitle("Aggiungi Attuatore");
            popupStage.showAndWait();
            if (controller.isActuatorAdded()) {
                reloadActuatorListView();
            }
        });
        contextMenu.getItems().add(addActuator);

        Actuator selectedActuator = listViewActuators.getSelectionModel().getSelectedItem();
        if (selectedActuator != null) {
            MenuItem removeActuator = new MenuItem("Rimuovi Attuatore");
            removeActuator.setOnAction(actionEvent -> JFXUtils.startVoidServiceTask(() -> {
                try {
                    selectedActuator.delete();
                    reloadActuatorListView();
                } catch (SQLException e) {
                    Client.showMessageAndGoToMenu(e);
                }
            }));
            contextMenu.getItems().add(removeActuator);
        }

        MenuItem clearSelection = new MenuItem("Svuota Selezione");
        clearSelection.setOnAction(actionEvent -> listViewActuators.getSelectionModel().clearSelection());
        contextMenu.getItems().add(clearSelection);

        contextMenu.setAutoHide(true);
        contextMenu.show(Client.getStage(), event.getScreenX(), event.getScreenY());
    }
    private void reloadActuatorListView() {
        JFXUtils.startVoidServiceTask(() -> {
            try {
                loadActuatorsFromDB();
                Platform.runLater(() -> {
                    listViewActuators.getItems().clear();
                    listViewActuators.getItems().setAll(actuators);
                });
            } catch (SQLException e) {
                Client.showMessageAndGoToMenu(e);
            }
        });
    }
    private void loadActuatorsFromDB() throws SQLException {
        actuators.clear();
        String query = "SELECT id FROM actuators;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            actuators.add(new Actuator(result.getLong("id")));
        }
        ps.close();
    }

}
