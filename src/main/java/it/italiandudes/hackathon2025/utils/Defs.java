package it.italiandudes.hackathon2025.utils;

public final class Defs {

    // App File Name
    public static final String APP_FILE_NAME = "Hackathon2025";

    // Logger Context
    public static final String LOGGER_CONTEXT = "Hackathon2025";

    // ID Launcher Support Flag
    public static final boolean IS_ID_LAUNCHER_SUPPORTED = false;

    // JSON Settings
    public static final class SettingsKeys {
        public static final String ENABLE_DARK_MODE = "enableDarkMode";
    }

    // Resources Location
    public static final class Resources {

        // Project Resources Root
        public static final String PROJECT_RESOURCES_ROOT = "/it/italiandudes/hackathon2025/resources/";

        // DB Extension
        public static final String DRYPE_EXTENSION = "drypedb";

        // JSON
        public static final class JSON {
            public static final String CLIENT_SETTINGS = "settings.json";
            public static final String DEFAULT_JSON_SETTINGS = PROJECT_RESOURCES_ROOT + "json/" + CLIENT_SETTINGS;
        }

        // SQL
        @SuppressWarnings("unused")
        public static final class SQL {
            private static final String SQL_DIR = PROJECT_RESOURCES_ROOT + "sql/";
            public static final String SQL_DB = SQL_DIR + "db.sql";
            public static String[] SUPPORTED_IMAGE_EXTENSIONS = {"png", "jpg", "jpeg"};
        }
    }
}
