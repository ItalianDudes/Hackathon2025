package it.italiandudes.hackathon2025.javafx.controllers.panel;

import it.italiandudes.hackathon2025.javafx.components.ConfigurableTabController;
import it.italiandudes.idl.javafx.JFXUtils;
import javafx.fxml.FXML;

public final class ControllerScenePanelTabForecast extends ConfigurableTabController {

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!super.isConfigurationComplete()) Thread.onSpinWait();
        });
    }

}
