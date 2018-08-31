package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethods;

import java.util.Arrays;
import java.util.logging.Logger;

public class AHSVIPreSolveMethod extends PreSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;

    public AHSVIPreSolveMethod(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }

    @Override
    public void init(HSVIAlgorithm hsvi, SolveMethods solveMethods) {
        super.init(hsvi, solveMethods);
        minValueFinder.init(hsvi);
    }

    @Override
    public void overridableMethod() {
        double[] minLbValueBelief = minValueFinder.findBeliefInLbMin();
        hsvi.getPomdpProblem().setInitBelief(minLbValueBelief);
        solveMethodsContainer.setLbVal(hsvi.getLBValueInBelief(minLbValueBelief));
        double[] minUbValueBelief = minValueFinder.findBeliefInUbMin();
        solveMethodsContainer.setUbVal(hsvi.getUBValueInBelief(minUbValueBelief));
        LOGGER.finer("LB min belief: " + Arrays.toString(minLbValueBelief));
        LOGGER.fine("LB min value: " + solveMethodsContainer.getLbVal());
        LOGGER.finer("UB min belief: " + Arrays.toString(minUbValueBelief));
        LOGGER.fine("UB min value: " + solveMethodsContainer.getUbVal());
    }
}
