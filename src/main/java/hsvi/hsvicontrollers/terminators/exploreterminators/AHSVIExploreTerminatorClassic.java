package hsvi.hsvicontrollers.terminators.exploreterminators;

public class AHSVIExploreTerminatorClassic extends ExploreTerminator {
    public AHSVIExploreTerminatorClassic() {
        super();
    }

    @Override
    protected boolean shouldTerminate(ExploreTerminatorParameters exploreTerminatorParameters) {
        return exploreTerminatorParameters.getT() >= solveMethodsContainer.getIteration();
    }
}
