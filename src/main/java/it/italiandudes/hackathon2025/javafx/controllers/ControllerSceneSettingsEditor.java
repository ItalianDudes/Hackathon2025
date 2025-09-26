package it.italiandudes.hackathon2025.javafx.controllers;

import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.javafx.JFXDefs;
import it.italiandudes.hackathon2025.javafx.scene.SceneMainMenu;
import it.italiandudes.hackathon2025.javafx.utils.Settings;
import it.italiandudes.hackathon2025.utils.Defs;
import it.italiandudes.idl.common.ResourceGetter;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.idl.javafx.alert.InformationAlert;
import it.italiandudes.idl.javafx.theme.BasicTheme;
import it.italiandudes.idl.javafx.theme.BasicThemeHandler;
import it.italiandudes.idl.logger.Logger;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONException;

import java.io.IOException;

public final class ControllerSceneSettingsEditor {

    // Attributes
    private static final Image DARK_MODE = new Image(ResourceGetter.getResource(JFXDefs.Resources.Image.IMAGE_DARK_MODE).toString());
    private static final Image LIGHT_MODE = new Image(ResourceGetter.getResource(JFXDefs.Resources.Image.IMAGE_LIGHT_MODE).toString());

    // Graphic Elements
    @FXML private ImageView imageViewEnableDarkMode;
    @FXML private ToggleButton toggleButtonEnableDarkMode;

    // Initialize
    @FXML
    private void initialize() {
        toggleButtonEnableDarkMode.setSelected(Settings.getSettings().getBoolean(Defs.SettingsKeys.ENABLE_DARK_MODE));
        if (toggleButtonEnableDarkMode.isSelected()) imageViewEnableDarkMode.setImage(DARK_MODE);
        else imageViewEnableDarkMode.setImage(LIGHT_MODE);
    }

    // EDT
    @FXML
    private void toggleEnableDarkMode() {
        if (toggleButtonEnableDarkMode.isSelected()) {
            imageViewEnableDarkMode.setImage(DARK_MODE);
            BasicThemeHandler.getInstance().loadDarkTheme(Client.getStage().getScene());
        }
        else {
            imageViewEnableDarkMode.setImage(LIGHT_MODE);
            BasicThemeHandler.getInstance().loadLightTheme(Client.getStage().getScene());
        }
    }
    @FXML
    private void backToMenu() {
        BasicThemeHandler.getInstance().setAndLoadTheme(Client.getStage().getScene(), BasicThemeHandler.getInstance().getCurrentTheme());
        Client.setScene(SceneMainMenu.getScene());
    }
    @FXML
    private void save() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            Settings.getSettings().put(Defs.SettingsKeys.ENABLE_DARK_MODE, toggleButtonEnableDarkMode.isSelected());
                        } catch (JSONException e) {
                            Logger.log(e, Defs.LOGGER_CONTEXT);
                        }
                        BasicTheme newTheme = toggleButtonEnableDarkMode.isSelected() ? BasicTheme.DARK : BasicTheme.LIGHT;
                        BasicThemeHandler.getInstance().setAndLoadTheme(Client.getStage().getScene(), newTheme);
                        try {
                            Settings.writeJSONSettings();
                            Platform.runLater(() -> new InformationAlert(Client.getStage(), "SUCCESSO", "Salvataggio Impostazioni", "Impostazioni salvate e applicate con successo!"));
                        } catch (IOException e) {
                            Logger.log(e, Defs.LOGGER_CONTEXT);
                            Platform.runLater(() -> new ErrorAlert(Client.getStage(), "ERRORE", "Errore di I/O", "Si e' verificato un errore durante il salvataggio delle impostazioni."));
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
}
