package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethod;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class InSolveMethod extends SolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public InSolveMethod() {
        super();
    }

    @Override
    public void solveStageSpecificMethod() {
        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Solve iteration: " + solveMethodsContainer.getIteration());
        LOGGER.finer("Running time so far [s]: " + solveMethodsContainer.getTimeSinceStarted());
        LOGGER.finest("LB size: " + hsvi.getLbFunction().getAlphaVectors().size());
        LOGGER.finest("UB size: " + hsvi.getUbFunction().getPoints().size());
    }
}
