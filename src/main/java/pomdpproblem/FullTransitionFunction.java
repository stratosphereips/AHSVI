package pomdpproblem;

public class FullTransitionFunction implements TransitionFunction {

    private final double[][][] transitionProbabilities;

    public FullTransitionFunction(double[][][] transitionProbabilities) {
        this.transitionProbabilities = transitionProbabilities;
    }

    @Override
    public double getProbability(int s, int a, int s_) {
        return transitionProbabilities[s][a][s_];
    }
}
