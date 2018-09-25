package pomdpproblem;

import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    public Iterator<Integer> getIteratorOverReachableStates(int s, int a) {
        return new S_Iterator(new ArrayList<>(transitionProbabilities.get(s).get(a).keySet()));
    }

    private class S_Iterator implements Iterator<Integer> {
        private final List<Integer> reachableStates;
        private int currStateI;

        public S_Iterator(List<Integer> reachableStates) {
            this.reachableStates = reachableStates;
            currStateI = 0;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext_ = currStateI < reachableStates.size();
            if (!hasNext_) {
                currStateI = 0;
            }
            return hasNext_;
        }

        @Override
        public Integer next() {
            ++currStateI;
            return reachableStates.get(currStateI - 1);
        }
    }
}
