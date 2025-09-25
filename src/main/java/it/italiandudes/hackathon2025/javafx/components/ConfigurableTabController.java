package it.italiandudes.hackathon2025.javafx.components;

import it.italiandudes.hackathon2025.javafx.controllers.panel.ControllerScenePanelMain;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigurableTabController {

    // Configuration
    private ControllerScenePanelMain mainController = null;
    private volatile boolean configurationComplete = false;

    // Methods
    public boolean isConfigurationComplete() {
        return configurationComplete;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }
    public void setMainController(@NotNull final ControllerScenePanelMain mainController) {
        this.mainController = mainController;
    }
    @NotNull
    public ControllerScenePanelMain getMainController() {
        if (!configurationComplete) throw new RuntimeException("Configuration not completed yet!");
        return mainController;
    }
}
