package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AHSVIInSolveMethod extends InSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;
    private final Map<String, Map<String, Set<Integer>>> infoSets;

    public AHSVIInSolveMethod(AHSVIMinValueFinder minValueFinder, Map<String, Map<String, Set<Integer>>> infoSets) {
        super();
        this.minValueFinder = minValueFinder;
        this.infoSets = infoSets;
    }

    @Override
    public void overridableMethod() {
        double[] minLbValueBelief = minValueFinder.findBeliefInLbMin();
        hsvi.getPomdpProblem().setInitBelief(minLbValueBelief);
        solveMethodsContainer.setNewLbVal(hsvi.getLBValueInBelief(minLbValueBelief));
        double[] minUbValueBelief = minValueFinder.findBeliefInUbMin();

        solveMethodsContainer.setNewUbVal(hsvi.getUBValueInBelief(minUbValueBelief));
        LOGGER.fine("LB_MIN_BELIEF: " + Arrays.toString(minLbValueBelief));
        LOGGER.fine("LB_MIN_VALUE: " + solveMethodsContainer.getLbVal());
        LOGGER.fine("LB_MIN_BELIEF_UB_VALUE: " + hsvi.getUbFunction().getValue(minLbValueBelief));
        LOGGER.finer(String.format(" ----- LB_MIN_VALUE_DIFF: %.20f",
                (solveMethodsContainer.getLbVal() - solveMethodsContainer.getLastLbVal())));
        LOGGER.fine("LB_MIN_BELIEF_ACTION: " +
                hsvi.getPomdpProblem().getActionName(
                        hsvi.getLbFunction().getDotProdArgMax(minLbValueBelief).a));
        LOGGER.fine("UB_MIN_BELIEF: " + Arrays.toString(minUbValueBelief));
        LOGGER.fine("UB_MIN_VALUE: " + solveMethodsContainer.getUbVal());
        LOGGER.finer(String.format(" ----- UB_MIN_VALUE_DIFF: %.20f",
                (solveMethodsContainer.getUbVal() - solveMethodsContainer.getLastUbVal())));
        LOGGER.fine("UB_MIN_BELIEF_ACTION: " +
                hsvi.getPomdpProblem().getActionName(
                        hsvi.getLbFunction().getDotProdArgMax(minUbValueBelief).a));
        LOGGER.fine("BOUNDS_GAP: " + (solveMethodsContainer.getLastUbVal() - solveMethodsContainer.getLastLbVal()));

        //LOGGER.fine(hsvi.getLbFunction().getAlphaVectors().stream().map(a -> Arrays.toString(a.vector)).collect(Collectors.joining("\n")));

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
