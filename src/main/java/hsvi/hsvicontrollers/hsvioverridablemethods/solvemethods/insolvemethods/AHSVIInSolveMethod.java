package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AHSVIInSolveMethod extends InSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;

    public AHSVIInSolveMethod(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }

    @Override
    public void overridableMethod() {
        double[] minLbValueBelief = minValueFinder.findBeliefInLbMin();
        hsvi.getPomdpProblem().setInitBelief(minLbValueBelief);
        solveMethodsContainer.setNewLbVal(hsvi.getLBValueInBelief(minLbValueBelief));
        double[] minUbValueBelief = minValueFinder.findBeliefInUbMin();
        solveMethodsContainer.setNewUbVal(hsvi.getUBValueInBelief(minUbValueBelief));
        LOGGER.finer("LB min belief: " + Arrays.toString(minLbValueBelief));
        LOGGER.fine("LB min value: " + solveMethodsContainer.getLbVal());
        LOGGER.fine(String.format(" ----- Diff to last iteration: %.20f\n",
                (solveMethodsContainer.getLbVal() - solveMethodsContainer.getLastLbVal())));
        LOGGER.finer("UB min belief: " + Arrays.toString(minUbValueBelief));
        LOGGER.fine("UB min value: " + solveMethodsContainer.getUbVal());
        LOGGER.fine(String.format(" ----- Diff to last iteration: %.20f\n",
                (solveMethodsContainer.getUbVal() - solveMethodsContainer.getLastUbVal())));

        //LOGGER.fine(hsvi.getLbFunction().getAlphaVectors().stream().map(a -> Arrays.toString(a.vector)).collect(Collectors.joining("\n")));
    }
}
