package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Terrain;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.controllers.panel.popup.ControllerScenePanelMainPopupAddSector;
import it.italiandudes.hackathon2025.javafx.controllers.panel.popup.ControllerScenePanelMainPopupAddTerrain;
import it.italiandudes.hackathon2025.javafx.scene.panel.*;
import it.italiandudes.hackathon2025.javafx.scene.panel.popup.ScenePanelMainPopupAddSector;
import it.italiandudes.hackathon2025.javafx.scene.panel.popup.ScenePanelMainPopupAddTerrain;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.components.SceneController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class ControllerScenePanelMain {

    // Attributes
    @NotNull private final ArrayList<@NotNull Terrain> terrains = new ArrayList<>();

    // Graphic Elements
    @FXML private ListView<Terrain> listViewTerrains;
    @FXML private ListView<Sector> listViewSectors;
    @FXML private TabPane tabPaneSector;
    @FXML private Tab tabAnalytics;
    @FXML private Tab tabTrends;
    @FXML private Tab tabSensors;
    @FXML private Tab tabActuators;

    // Tab SceneControllers
    private SceneController sceneControllerAnalytics = null;
    private SceneController sceneControllerTrends = null;
    private SceneController sceneControllerSensors = null;
    private SceneController sceneControllerActuators = null;

    // Controller Access
    @NotNull
    public ControllerScenePanelTabDashboard getControllerAnalytics() {
        return (ControllerScenePanelTabDashboard) sceneControllerAnalytics.getController();
    }
    @NotNull
    public ControllerScenePanelTabSensors getControllerSensors() {
        return (ControllerScenePanelTabSensors) sceneControllerSensors.getController();
    }
    @NotNull
    public ControllerScenePanelTabActuators getControllerActuators() {
        return (ControllerScenePanelTabActuators) sceneControllerActuators.getController();
    }
    @NotNull
    public ControllerScenePanelTabGraphs getControllerGraphs() {
        return (ControllerScenePanelTabGraphs) sceneControllerTrends.getController();
    }

    // Initialize
    @FXML
    private void initialize() {
        listViewTerrains.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTerrain, newTerrain) -> {
            if (newTerrain == null) {
                listViewSectors.setVisible(false);
                listViewSectors.getItems().clear();
                tabPaneSector.setVisible(false);
            } else {
                listViewSectors.getItems().clear();
                listViewSectors.getItems().setAll(newTerrain.getSectors());
                listViewSectors.setVisible(true);
                tabPaneSector.setVisible(false);
            }
        });
        listViewSectors.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSector, newSector) -> {
            if (newSector == null) {
                tabPaneSector.setVisible(false);
                tabPaneSector.getSelectionModel().selectFirst();
            } else {
                tabPaneSector.getSelectionModel().selectFirst();
                tabPaneSector.setVisible(true);
                refreshTabPaneData();
            }
        });

        sceneControllerAnalytics = ScenePanelTabDashboard.getScene(this);
        tabAnalytics.setContent(sceneControllerAnalytics.getParent());

        sceneControllerTrends = ScenePanelTabGraphs.getScene(this);
        tabTrends.setContent(sceneControllerTrends.getParent());

        sceneControllerSensors = ScenePanelTabSensors.getScene(this);
        tabSensors.setContent(sceneControllerSensors.getParent());

        sceneControllerActuators = ScenePanelTabActuators.getScene(this);
        tabActuators.setContent(sceneControllerActuators.getParent());

        reloadTerrainListView();
    }

    // Refresh Data
    public void refreshTabPaneData() {
        getControllerAnalytics().refreshTerrainAndSectorInfo();
    }

    // EDT
    @FXML
    private void showTerrainsContextMenu(@NotNull ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addTerrain = new MenuItem("Aggiungi Terreno");
        addTerrain.setOnAction(actionEvent -> {
            SceneController popupSceneController = ScenePanelMainPopupAddTerrain.getScene();
            ControllerScenePanelMainPopupAddTerrain controller = (ControllerScenePanelMainPopupAddTerrain) popupSceneController.getController();
            Stage popupStage = Client.initPopupStage(popupSceneController);
            popupStage.setTitle("Aggiungi Terreno");
            popupStage.showAndWait();
            if (controller.isTerrainAdded()) {
                reloadTerrainListView();
            }
        });
        contextMenu.getItems().add(addTerrain);

        Terrain selectedTerrain = listViewTerrains.getSelectionModel().getSelectedItem();
        if (selectedTerrain != null) {
            MenuItem removeTerrain = new MenuItem("Rimuovi Terreno");
            removeTerrain.setOnAction(actionEvent -> JFXUtils.startVoidServiceTask(() -> {
                try {
                    selectedTerrain.delete();
                    reloadTerrainListView();
                } catch (SQLException e) {
                    Client.showMessageAndGoToMenu(e);
                }
            }));
            contextMenu.getItems().add(removeTerrain);
        }

        MenuItem clearSelection = new MenuItem("Svuota Selezione");
        clearSelection.setOnAction(actionEvent -> listViewTerrains.getSelectionModel().clearSelection());
        contextMenu.getItems().add(clearSelection);

        contextMenu.setAutoHide(true);
        contextMenu.show(Client.getStage(), event.getScreenX(), event.getScreenY());
    }
    @FXML
    private void showSectorsContextMenu(@NotNull ContextMenuEvent event) {
        Terrain selectedTerrain = listViewTerrains.getSelectionModel().getSelectedItem();
        if (selectedTerrain == null) return;
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addSector = new MenuItem("Aggiungi Settore");
        addSector.setOnAction(actionEvent -> {
            SceneController popupSceneController = ScenePanelMainPopupAddSector.getScene(selectedTerrain);
            ControllerScenePanelMainPopupAddSector controller = (ControllerScenePanelMainPopupAddSector) popupSceneController.getController();
            Stage popupStage = Client.initPopupStage(popupSceneController);
            popupStage.setTitle("Aggiungi Settore");
            popupStage.showAndWait();
            if (controller.isSectorAdded()) {
                reloadTerrainListView();
            }
        });
        contextMenu.getItems().add(addSector);

        Sector selectedSector = listViewSectors.getSelectionModel().getSelectedItem();
        if (selectedSector != null) {
            MenuItem removeTerrain = new MenuItem("Rimuovi Settore");
            removeTerrain.setOnAction(actionEvent -> JFXUtils.startVoidServiceTask(() -> {
                try {
                    selectedSector.delete();
                    reloadTerrainListView();
                } catch (SQLException e) {
                    Client.showMessageAndGoToMenu(e);
                }
            }));
            contextMenu.getItems().add(removeTerrain);
        }

        MenuItem clearSelection = new MenuItem("Svuota Selezione");
        clearSelection.setOnAction(actionEvent -> listViewSectors.getSelectionModel().clearSelection());
        contextMenu.getItems().add(clearSelection);

        contextMenu.setAutoHide(true);
        contextMenu.show(Client.getStage(), event.getScreenX(), event.getScreenY());
    }

    // Methods
    private void reloadTerrainListView() {
        JFXUtils.startVoidServiceTask(() -> {
            try {
                loadTerrainsFromDB();
                Platform.runLater(() -> {
                    listViewTerrains.getItems().clear();
                    listViewTerrains.getItems().setAll(terrains);
                });
            } catch (SQLException e) {
                Client.showMessageAndGoToMenu(e);
            }
        });
    }
    private void loadTerrainsFromDB() throws SQLException {
        terrains.clear();
        String query = "SELECT id FROM terrains;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            terrains.add(new Terrain(result.getLong("id")));
        }
        ps.close();
    }
    public Terrain getSelectedTerrain() {
        return listViewTerrains.getSelectionModel().getSelectedItem();
    }
    public Sector getSelectedSector() {
        if (getSelectedTerrain() != null) return listViewSectors.getSelectionModel().getSelectedItem();
        return null;
    }
}
