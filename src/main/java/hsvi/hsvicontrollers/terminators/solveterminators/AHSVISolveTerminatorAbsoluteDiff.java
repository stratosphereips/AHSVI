package hsvi.hsvicontrollers.terminators.solveterminators;

public class AHSVISolveTerminatorAbsoluteDiff extends SolveTerminator {

    public AHSVISolveTerminatorAbsoluteDiff() {
        super();
    }

    @Override
    protected boolean shouldTerminate(SolveTerminatorParameters exploreTerminatorParameters) {
        return solveMethodsContainer.getUbVal() - solveMethodsContainer.getLbVal() <= hsvi.getEpsilon();
    }
}
