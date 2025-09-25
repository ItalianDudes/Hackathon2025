package it.italiandudes.hackathon2025.javafx.scene.panel;

import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.JFXDefs;
import it.italiandudes.hackathon2025.javafx.controllers.panel.ControllerScenePanelMain;
import it.italiandudes.hackathon2025.javafx.controllers.panel.ControllerScenePanelTabTrends;
import it.italiandudes.hackathon2025.utils.Defs;
import it.italiandudes.idl.common.ResourceGetter;
import it.italiandudes.idl.javafx.components.SceneController;
import it.italiandudes.idl.logger.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public final class ScenePanelTabTrends {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final ControllerScenePanelMain mainController) {
        return Objects.requireNonNull(genScene(mainController));
    }
    @Nullable
    private static SceneController genScene(@NotNull final ControllerScenePanelMain mainController) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceGetter.getResource(JFXDefs.Resources.FXML.Panel.FXML_PANEL_TAB_TRENDS));
            Parent root = loader.load();
            ControllerScenePanelTabTrends controller = loader.getController();
            controller.setMainController(mainController);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
            Client.exit(-1);
            return null;
        }
    }
}
