package it.italiandudes.hackathon2025.javafx;

import it.italiandudes.idl.common.ResourceGetter;
import it.italiandudes.hackathon2025.utils.Defs;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;

@SuppressWarnings("unused")
public final class JFXDefs {

    // App Info
    public static final class AppInfo {
        public static final String NAME = "Drype Terrains&Sectors Manager";
        public static final Image LOGO = new Image(ResourceGetter.getResource(Resources.Image.Logo.LOGO_MAIN).toString());
    }

    // System Info
    public static final class SystemGraphicInfo {
        public static final Rectangle2D SCREEN_RESOLUTION = Screen.getPrimary().getBounds();
        public static final double SCREEN_WIDTH = SCREEN_RESOLUTION.getWidth();
        public static final double SCREEN_HEIGHT = SCREEN_RESOLUTION.getHeight();
    }

    // Resource Locations
    public static final class Resources {

        // Project Resources Root
        public static final String PROJECT_RESOURCES_ROOT = Defs.Resources.PROJECT_RESOURCES_ROOT;

        // FXML Location
        public static final class FXML {
            private static final String FXML_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "fxml/";
            public static final String FXML_LOADING = FXML_DIR + "SceneLoading.fxml";
            public static final String FXML_MAIN_MENU = FXML_DIR + "SceneMainMenu.fxml";
            public static final String FXML_SETTINGS_EDITOR = FXML_DIR + "SceneSettingsEditor.fxml";
            public static final class Panel {
                private static final String FXML_PANEL = FXML_DIR + "panel/";
                public static final String FXML_PANEL_MAIN = FXML_PANEL + "ScenePanelMain.fxml";
                public static final String FXML_PANEL_TAB_ANALYTICS = FXML_PANEL + "ScenePanelTabDashboard.fxml";
                public static final String FXML_PANEL_TAB_TRENDS = FXML_PANEL + "ScenePanelTabTrends.fxml";
                public static final String FXML_PANEL_TAB_CONTROLS = FXML_PANEL + "ScenePanelTabControls.fxml";
                public static final String FXML_PANEL_TAB_FORECAST = FXML_PANEL + "ScenePanelTabForecast.fxml";
                public static final String FXML_PANEL_TAB_SENSORS = FXML_PANEL + "ScenePanelTabSensors.fxml";
                public static final String FXML_PANEL_TAB_ACTUATORS = FXML_PANEL + "ScenePanelTabActuators.fxml";
                public static final class Popup {
                    private static final String FXML_POPUP = FXML_PANEL + "popup/";
                    public static final String FXML_POPUP_ADD_TERRAIN = FXML_POPUP + "ScenePanelMainPopupAddTerrain.fxml";
                    public static final String FXML_POPUP_ADD_SECTOR = FXML_POPUP + "ScenePanelMainPopupAddSector.fxml";
                    public static final String FXML_POPUP_ADD_SENSOR = FXML_POPUP + "ScenePanelMainPopupAddSensor.fxml";
                    public static final String FXML_POPUP_ADD_ACTUATOR = FXML_POPUP + "ScenePanelMainPopupAddActuator.fxml";
                }
            }
        }

        // GIF Location
        public static final class GIF {
            private static final String GIF_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "gif/";
            public static final String GIF_LOADING = GIF_DIR+"loading.gif";
        }

        // CSS Location
        public static final class CSS {
            private static final String CSS_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "css/";
            public static final String CSS_LIGHT_THEME = CSS_DIR + "light_theme.css";
            public static final String CSS_DARK_THEME = CSS_DIR + "dark_theme.css";
        }

        // Image Location
        public static final class Image {
            private static final String IMAGE_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "image/";
            public static final class Logo {
                private static final String LOGO_DIR = IMAGE_DIR + "logo/";
                public static final String LOGO_MAIN = LOGO_DIR + "main.png";
            }
            public static final String IMAGE_FILE_EXPLORER = IMAGE_DIR + "file-explorer.png";
            public static final String IMAGE_DARK_MODE = IMAGE_DIR + "dark_mode.png";
            public static final String IMAGE_LIGHT_MODE = IMAGE_DIR + "light_mode.png";
        }

    }

}
