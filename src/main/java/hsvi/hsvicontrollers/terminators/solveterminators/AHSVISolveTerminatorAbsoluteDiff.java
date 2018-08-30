package hsvi.hsvicontrollers.terminators.solveterminators;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;

public class AHSVISolveTerminatorAbsoluteDiff extends SolveTerminator {
    private final AHSVIMinValueFinder minValueFinder;

    public AHSVISolveTerminatorAbsoluteDiff(AHSVIMinValueFinder minValueFinder) {
        super();
        this.minValueFinder = minValueFinder;
    }

    @Override
    protected boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters) {
        double[] beliefInLbMin = hsvi.getPomdpProblem().initBelief;
        double valueInLbMin = hsvi.getLBValueInBelief(beliefInLbMin);
        double[] beliefInUbMin = minValueFinder.findBeliefInUbMin();
        double valueInUbMin = hsvi.getUBValueInBelief(beliefInUbMin);
        return valueInUbMin - valueInLbMin <= hsvi.getEpsilon();
    }
}
