package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.Terminator;
import hsvi.hsvicontrollers.terminators.TerminatorParameters;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorParameters;

public abstract class ExploreTerminator extends Terminator {

    public ExploreTerminator() {
        super();
    }

    @Override
    public boolean shouldTerminate(TerminatorParameters terminatorParameters) {
        if (!(terminatorParameters instanceof ExploreTerminatorParameters)) {
            throw new IllegalArgumentException("Expected instance of ExploreTerminatorParameters");
        }
        ExploreTerminatorParameters etp = (ExploreTerminatorParameters)terminatorParameters;
        return shouldTerminate(etp);
    }

    protected abstract boolean shouldTerminate(ExploreTerminatorParameters exploreTerminatorParameters);

    public ExploreTerminatorParameters createParametersContainer() {
        return new ExploreTerminatorParameters();
    }
}
