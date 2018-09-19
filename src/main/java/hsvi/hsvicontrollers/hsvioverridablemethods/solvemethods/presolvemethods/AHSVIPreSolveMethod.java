package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.HSVIAlgorithm;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethods;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class AHSVIPreSolveMethod extends PreSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;
    private final Map<String, Map<String, Set<Integer>>> infoSets;

    public AHSVIPreSolveMethod(AHSVIMinValueFinder minValueFinder, Map<String, Map<String, Set<Integer>>> infoSets) {
        super();
        this.minValueFinder = minValueFinder;
        this.infoSets = infoSets;
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
        LOGGER.fine("LB_MIN_BELIEF: " + Arrays.toString(minLbValueBelief));
        LOGGER.fine("LB_MIN_VALUE: " + solveMethodsContainer.getLbVal());
        LOGGER.fine("LB_MIN_BELIEF_UB_VALUE: " + hsvi.getUbFunction().getValue(minLbValueBelief));
        LOGGER.fine("UB_MIN_BELIEF: " + Arrays.toString(minUbValueBelief));
        LOGGER.fine("UB_MIN_VALUE: " + solveMethodsContainer.getUbVal());

        logInfoSets();
    }

    protected void logInfoSets() {
        Set<String> infoSetsKeySet = infoSets.keySet();
        Map<String, Set<Integer>> combinationGroupsInInfoSet;
        Set<String> combinationGroupsKeySet;
        Set<Integer> combinationGroup;
        for (String infoSetName : infoSetsKeySet) {
            LOGGER.fine(infoSetName);
            combinationGroupsInInfoSet = infoSets.get(infoSetName);
            combinationGroupsKeySet = combinationGroupsInInfoSet.keySet();
            for (String combinationGroupName : combinationGroupsKeySet) {
                combinationGroup = combinationGroupsInInfoSet.get(combinationGroupName);
                LOGGER.fine("\t" + combinationGroupName);
                for (Integer s : combinationGroup) {
                    LOGGER.fine("\t\t" + hsvi.getPomdpProblem().getInitBelief(s) + "   " + hsvi.getPomdpProblem().getStateName(s));
                }
            }
        }
    }
}
