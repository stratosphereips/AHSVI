package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AHSVIPostSolveMethod extends PostSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;
    private final Map<String, Map<String, Set<Integer>>> infoSets;

    public AHSVIPostSolveMethod(AHSVIMinValueFinder minValueFinder, Map<String, Map<String, Set<Integer>>> infoSets) {
        super();
        this.minValueFinder = minValueFinder;
        this.infoSets = infoSets;
    }

    @Override
    public void overridableMethod() {
        hsvi.getPomdpProblem().setInitBelief(minValueFinder.findBeliefInUbMin());

        logInfoSets(hsvi.getPomdpProblem().getInitBelief());
    }

    protected void logInfoSets(double[] minUbValueBelief) {
        Set<String> infoSetsKeySet = infoSets.keySet();
        Map<String, Set<Integer>> combinationGroupsInInfoSet;
        Set<String> combinationGroupsKeySet;
        Set<Integer> combinationGroup;
        double combinationProbSum;
        for (String infoSetName : infoSetsKeySet) {
            LOGGER.fine(infoSetName);
            combinationGroupsInInfoSet = infoSets.get(infoSetName);
            combinationGroupsKeySet = combinationGroupsInInfoSet.keySet();
            for (String combinationGroupName : combinationGroupsKeySet) {
                combinationGroup = combinationGroupsInInfoSet.get(combinationGroupName);
                combinationProbSum = 0;
                for (Integer s : combinationGroup) {
                    combinationProbSum += minUbValueBelief[s];
                }
                LOGGER.fine(String.format("\t%s  %.4f", combinationGroupName, combinationProbSum));
                for (Integer s : combinationGroup) {
                    LOGGER.fine(String.format("\t\t%.4f  %s",
                            minUbValueBelief[s],
                            hsvi.getPomdpProblem().getStateName(s) ));
                }
            }
        }
    }
}
