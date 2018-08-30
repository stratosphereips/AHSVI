package hsvi.hsvicontrollers.terminators.solveterminators;

import hsvi.hsvicontrollers.terminators.TerminatorParameters;

public class SolveTerminatorParameters implements TerminatorParameters {
    private double[] belief;

    public SolveTerminatorParameters() {
        belief = null;
    }

    public double[] getBelief() {
        return belief;
    }

    public TerminatorParameters setBelief(double[] belief) {
        this.belief = belief;
        return this;
    }
}
