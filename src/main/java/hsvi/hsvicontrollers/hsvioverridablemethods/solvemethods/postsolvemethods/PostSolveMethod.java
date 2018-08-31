package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.OverridableMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.InitializableWithHSVIAndSolveMethods;

import java.util.logging.Logger;

public abstract class PostSolveMethod extends InitializableWithHSVIAndSolveMethods implements OverridableMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public PostSolveMethod() {
        super();
    }

    @Override
    public void callMethod() {

        LOGGER.fine("###########################################################################");
        LOGGER.fine("###########################################################################");
        LOGGER.fine("Finish in solve iteration: " + solveMethodsContainer.getIteration());

    }
}
