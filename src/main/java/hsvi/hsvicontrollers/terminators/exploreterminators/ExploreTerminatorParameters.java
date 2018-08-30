package hsvi.hsvicontrollers.terminators.exploreterminators;

import hsvi.hsvicontrollers.terminators.TerminatorParameters;

public class ExploreTerminatorParameters implements TerminatorParameters {
    private double[] belief;
    private int t;

    public ExploreTerminatorParameters() {
        belief = null;
        t = -1;
    }

    public double[] getBelief() {
        return belief;
    }

    public int getT() {
        return t;
    }

    public TerminatorParameters setParameters(double[] belief, int t) {
        this.belief = belief;
        this.t = t;
        return this;
    }
}
