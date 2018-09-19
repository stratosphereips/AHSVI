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
