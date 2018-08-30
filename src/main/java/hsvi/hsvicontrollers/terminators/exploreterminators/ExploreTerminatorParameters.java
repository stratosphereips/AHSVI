package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.TerminatorParameters;

public class ExploreTerminatorParameters implements TerminatorParameters {
    private double[] belief;
    private int t;
    private int iteration;

    public ExploreTerminatorParameters() {
        belief = null;
        t = -1;
        iteration = -1;
    }

    public double[] getBelief() {
        return belief;
    }

    public int getT() {
        return t;
    }

    public int getIteration() {
        return iteration;
    }

    public TerminatorParameters setParameters(double[] belief, int t, int iteration) {
        this.belief = belief;
        this.t = t;
        this.iteration = iteration;
        return this;
    }
}
