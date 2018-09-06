package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods;

import hsvi.CustomLogger.CustomLogger;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AHSVIPostSolveMethod extends PostSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    public AHSVIPostSolveMethod() {
        super();
    }

    @Override
    public void overridableMethod() {
        //LOGGER.fine(hsvi.getPomdpProblem().stateNames.stream().collect(Collectors.joining("\n")));
    }
}
