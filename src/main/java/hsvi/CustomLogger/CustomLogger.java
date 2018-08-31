package hsvi.CustomLogger;

import hsvi.Config;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class CustomLogger {

    private static Logger LOGGER = null;

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = Logger.getLogger("LOGGER");
            LOGGER.setLevel(Config.LOGGING_LEVEL);
            LOGGER.setUseParentHandlers(false);
            Handler hndl = new ConsoleHandler();
            hndl.setFormatter(new CustomRecordFormatter());
            hndl.setLevel(Config.LOGGING_LEVEL);
            LOGGER.addHandler(hndl);
        }
        return LOGGER;
    }
}
