package hsvi.hsvicontrollers.terminators.solveterminators;

public class SolveTerminatorInfinite extends SolveTerminator {
    public SolveTerminatorInfinite() {
        super();
    }

    @Override
    protected boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters) {
        return false;
    }
}
