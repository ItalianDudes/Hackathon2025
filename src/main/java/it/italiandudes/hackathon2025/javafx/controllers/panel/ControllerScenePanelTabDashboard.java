package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.data.Terrain;
import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.idl.javafx.JFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class ControllerScenePanelTabDashboard extends ConfigurableTabController {

    // Graphic Elements
    @FXML private Label labelTerrainID;
    @FXML private Label labelTerrainName;
    @FXML private Label labelOwner;
    @FXML private Label labelTerrainDimension;
    @FXML private Label labelSectorID;
    @FXML private Label labelSectorName;
    @FXML private Label labelSectorDimension;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
        });
    }

    // Methods
    public void loadTerrainAndSectorInfo() {
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
    }
}
