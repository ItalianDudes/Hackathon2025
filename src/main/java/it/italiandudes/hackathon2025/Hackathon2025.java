package it.italiandudes.hackathon2025;

import it.italiandudes.hackathon2025.javafx.Client;
import it.italiandudes.hackathon2025.utils.Defs;
import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.idl.starter.IDMain;

public final class Hackathon2025 {

    // Attributes
    private static volatile boolean appClosed = false;

    // Launcher Main Method
    @SuppressWarnings("unused")
    public static void launcherMain(ClassLoader loader, String[] args) {
        if (!Defs.IS_ID_LAUNCHER_SUPPORTED) {
            throw new RuntimeException(Defs.APP_FILE_NAME + " doesn't support launcher start!");
        }
        if (loader == null) {
            throw new RuntimeException("Launcher Main requires the launcher loader to be provided!");
        }
        IDMain.setLauncherClassLoader(loader);
        Thread.currentThread().setContextClassLoader(loader);
        main(args);
    }
    public static void appClosed() {
        appClosed = true;
    }

    // Launcher Blocker
    @SuppressWarnings("unused")
    public static void launcherLockUntilAppClose() {
        if (!Defs.IS_ID_LAUNCHER_SUPPORTED) return;
        while (!appClosed) Thread.onSpinWait();
    }

    // Main Method
    public static void main(String[] args) {

        // IDMain Default Main Initialization Procedure
        IDMain.defaultJ21Main(Defs.LOGGER_CONTEXT);

        // Start the client
        try {
            Logger.log("Starting UI...", Defs.LOGGER_CONTEXT);
            Client.start(args);
        } catch (NoClassDefFoundError e) {
            Logger.log("ERROR: JAVAFX NOT FOUND!", new InfoFlags(true, true, true, true), Defs.LOGGER_CONTEXT);
            Logger.log(e, Defs.LOGGER_CONTEXT);
            if (!IDMain.isStartedFromLauncher()) {
                Logger.close();
                System.exit(-1);
            }
        } catch (Exception e) {
            Logger.log("An exception has occurred while starting UI!", new InfoFlags(true, true, true, true), Defs.LOGGER_CONTEXT);
            Logger.log(e, Defs.LOGGER_CONTEXT);
            if (!IDMain.isStartedFromLauncher()) {
                Logger.close();
                System.exit(-1);
            }
        }
    }
}
