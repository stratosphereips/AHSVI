package hsvi.hsvicontrollers.terminators.solveterminators;

import hsvi.hsvicontrollers.terminators.Terminator;
import hsvi.hsvicontrollers.terminators.TerminatorParameters;

public abstract class SolveTerminator extends Terminator {

    public SolveTerminator() {
        super();
    }

    @Override
    public boolean shouldTerminate(TerminatorParameters terminatorParameters) {
        if (!(terminatorParameters instanceof TerminatorParameters)) {
            throw new IllegalArgumentException("Expected instance of SolveTerminatorParameters");
        }
        SolveTerminatorParameters etp = (SolveTerminatorParameters)terminatorParameters;
        return shouldTerminate(etp);
    }

    protected abstract boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters);

    public SolveTerminatorParameters createParametersContainer() {
        return new SolveTerminatorParameters();
    }
}
