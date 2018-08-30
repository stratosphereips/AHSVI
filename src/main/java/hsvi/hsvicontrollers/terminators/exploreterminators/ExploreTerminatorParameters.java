package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.TerminatorParameters;

public class ExploreTerminatorParameters implements TerminatorParameters {
    private int t;
    private int iteration;

    public ExploreTerminatorParameters() {
        t = -1;
        iteration = -1;
    }

    public int getT() {
        return t;
    }

    public int getIteration() {
        return iteration;
    }

    public TerminatorParameters setParameters(int t, int iteration) {
        this.iteration = iteration;
        return this;
    }
}
