package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorParameters;

public class AHSVISolveTerminatorAbsoluteDiff extends SolveTerminator {

    public AHSVISolveTerminatorAbsoluteDiff() {
        super();
    }

    @Override
    protected boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters) {
        double[] beliefInLbMin = exploreTerminatorParameters.getBelief();
        double valueInLbMin = hsvi.getLBValueInBelief(beliefInLbMin);
        double[] beliefInUbMin = hsvi.getUbFunction().getBeliefInMinimum();
        double valueInUbMin = hsvi.getUBValueInBelief(beliefInUbMin);
        return valueInUbMin - valueInLbMin <= epsilon;
    }
}