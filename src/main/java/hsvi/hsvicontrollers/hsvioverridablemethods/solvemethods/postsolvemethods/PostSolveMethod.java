package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethod;

import java.util.logging.Logger;

public abstract class PostSolveMethod extends SolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public PostSolveMethod() {
        super();
    }

    @Override
    public void solveStageSpecificMethod() {
        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Finish in solve iteration: " + solveMethodsContainer.getIteration());
    }
}
