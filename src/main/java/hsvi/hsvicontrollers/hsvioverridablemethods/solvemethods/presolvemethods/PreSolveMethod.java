package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethod;

import java.util.logging.Logger;

public abstract class PreSolveMethod extends SolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public PreSolveMethod() {
        super();
    }

    @Override
    public void solveStageSpecificMethod() {
        hsvi.initValueFunctions();

        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Starting solving iterations");
    }
}
