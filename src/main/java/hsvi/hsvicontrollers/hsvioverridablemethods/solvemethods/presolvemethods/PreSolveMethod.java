package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.OverridableMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.InitializableWithHSVIAndSolveMethods;

import java.util.logging.Logger;

public abstract class PreSolveMethod extends InitializableWithHSVIAndSolveMethods implements OverridableMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public PreSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        hsvi.initValueFunctions();

        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Starting solving iterations");
    }
}
