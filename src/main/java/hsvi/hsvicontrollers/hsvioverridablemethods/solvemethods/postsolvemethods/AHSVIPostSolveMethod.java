package hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods;

import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import sun.reflect.generics.tree.Tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AHSVIPostSolveMethod extends PostSolveMethod {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final AHSVIMinValueFinder minValueFinder;
    private final HashMap<String, TreeSet<Integer>> infoSets;

    public AHSVIPostSolveMethod(AHSVIMinValueFinder minValueFinder, HashMap<String, TreeSet<Integer>> infoSets) {
        super();
        this.minValueFinder = minValueFinder;
        this.infoSets = infoSets;
    }

    @Override
    public void overridableMethod() {
        hsvi.getPomdpProblem().setInitBelief(minValueFinder.findBeliefInUbMin());

        for (String infoSetName : infoSets.keySet()) {
            LOGGER.severe(infoSetName);
            for (Integer s : infoSets.get(infoSetName)) {
                LOGGER.severe("\t" + hsvi.getPomdpProblem().getInitBelief(s) + "   " + hsvi.getPomdpProblem().getStateName(s));
            }
        }
    }
}
