package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.OverridableMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.InitializableWithHSVIAndSolveMethods;

import java.util.logging.Logger;

public abstract class InSolveMethod extends InitializableWithHSVIAndSolveMethods implements OverridableMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public InSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {
        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Solve iteration: " + solveMethodsContainer.getIteration());
        LOGGER.finer("Running time so far [s]: " + solveMethodsContainer.getTimeSinceStarted());
    }
}
