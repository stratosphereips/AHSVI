package hsvi.hsvicontrollers.terminators.exploreterminators;

public class HSVIExploreTerminatorClassic extends ExploreTerminator {
    public HSVIExploreTerminatorClassic() {
        super();
    }

    @Override
    protected boolean shouldTerminate(ExploreTerminatorParameters exploreTerminatorParameters) {
        return hsvi.width(exploreTerminatorParameters.getBelief())
                <=
                hsvi.getEpsilon() * Math.pow(hsvi.getPomdpProblem().getDiscount(), -exploreTerminatorParameters.getT());
    }
}
