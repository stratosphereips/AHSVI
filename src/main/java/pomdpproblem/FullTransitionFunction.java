package pomdpproblem;

import java.util.Iterator;

public class FullTransitionFunction implements TransitionFunction {

    private final double[][][] transitionProbabilities;

    public FullTransitionFunction(double[][][] transitionProbabilities) {
        this.transitionProbabilities = transitionProbabilities;
    }

    @Override
    public double getProbability(int s, int a, int s_) {
        return transitionProbabilities[s][a][s_];
    }

    @Override
    public Iterator<Integer> getIteratorOverReachableStates(int s, int a) {
        return new S_Iterator(transitionProbabilities.length);
    }

    private class S_Iterator implements Iterator<Integer> {
        private int s;
        private final int statesCount;

        public S_Iterator(int statesCount) {
            s = 0;
            this.statesCount = statesCount;
        }

        @Override
        public boolean hasNext() {
            return s < statesCount;
        }

        @Override
        public Integer next() {
            ++s;
            return s - 1;
        }
    }
}
