package hsvi.hsvicontrollers.terminators;

import hsvi.HSVIAlgorithm;

public abstract class Terminator {
    protected HSVIAlgorithm hsvi;
    protected double epsilon;

    public Terminator() {
        hsvi = null;
        this.epsilon = -1;
    }

    public void init(HSVIAlgorithm hsvi, double epsilon) {
        this.hsvi = hsvi;
        this.epsilon = epsilon;
    }

    public abstract boolean shouldTerminate(TerminatorParameters terminatorParameters);
}
