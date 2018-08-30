package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorParameters;

public class HSVISolveTerminatorClassic extends SolveTerminator {
    public HSVISolveTerminatorClassic() {
        super();
    }

    @Override
    protected boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters) {
        return hsvi.width(exploreTerminatorParameters.getBelief()) <= epsilon;
    }
}
