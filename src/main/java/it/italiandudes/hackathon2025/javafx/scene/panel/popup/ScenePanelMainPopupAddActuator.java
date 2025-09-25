package it.italiandudes.hackathon2025.javafx.scene.panel.popup;

import it.italiandudes.hackathon2025.data.Sector;
import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.JFXDefs;
import it.italiandudes.hackathon2025.javafx.controllers.panel.popup.ControllerScenePanelMainPopupAddActuator;
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

public final class ScenePanelMainPopupAddActuator {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Sector sector) {
        return Objects.requireNonNull(genScene(sector));
    }
    @Nullable
    private static SceneController genScene(@NotNull final Sector sector) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceGetter.getResource(JFXDefs.Resources.FXML.Panel.Popup.FXML_POPUP_ADD_ACTUATOR));
            Parent root = loader.load();
            ControllerScenePanelMainPopupAddActuator controller = loader.getController();
            controller.setSector(sector);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
            Client.exit(-1);
            return null;
        }
    }
}
