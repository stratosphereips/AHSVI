package pomdpproblem;

import java.util.List;
import java.util.Map;

public class SparseTransitionFunction implements TransitionFunction {

    private final List<List<Map<Integer, Double>>> transitionProbabilities;

    public SparseTransitionFunction(List<List<Map<Integer, Double>>> transitionProbabilities) {
        this.transitionProbabilities = transitionProbabilities;
    }

    @Override
    public double getProbability(int s, int a, int s_) {
        return transitionProbabilities.get(s).get(a).getOrDefault(s_, 0.0);
    }
}
