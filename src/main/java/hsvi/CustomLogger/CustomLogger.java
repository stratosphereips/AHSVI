package hsvi.CustomLogger;

import hsvi.Config;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class CustomLogger {

    public static Logger getLogger(String className) {
        Logger LOGGER = Logger.getLogger(className);

        LOGGER.setLevel(Config.LOGGING_LEVEL);
        LOGGER.setUseParentHandlers(false);
        Handler hndl = new ConsoleHandler();
        hndl.setFormatter(new CustomRecordFormatter());
        hndl.setLevel(Config.LOGGING_LEVEL);
        LOGGER.addHandler(hndl);

        return LOGGER;
    }
}
