package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

import java.util.Arrays;
import java.util.logging.Logger;

public class AHSVIInSolveMethod extends InSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;

    public AHSVIInSolveMethod(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }

    @Override
    public void callMethod() {
        super.callMethod();
        hsvi.getPomdpProblem().setInitBelief(minValueFinder.findBeliefInLbMin());


    }
}
