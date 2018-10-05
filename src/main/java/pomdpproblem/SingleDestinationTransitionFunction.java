package pomdpproblem;

import java.util.*;

public class SingleDestinationTransitionFunction implements TransitionFunction {

    private final boolean[] isActionProbe;
    private final List<Set<Integer>> attacksEndingInSourceForStates;
    private final int finalS;

    public SingleDestinationTransitionFunction(int statesCount, int actionsCount) {
        isActionProbe = new boolean[actionsCount];
        attacksEndingInSourceForStates = new ArrayList<>(statesCount);
        for (int s = 0; s < statesCount; ++s) {
            attacksEndingInSourceForStates.add(new TreeSet<>());
        }
        finalS = statesCount - 1;
    }

    public void addProbe(int a) {
        isActionProbe[a] = true;
    }

    public void addStateAndActionPairEndingInSource(int s, int a) {
        attacksEndingInSourceForStates.get(s).add(a);
    }

    private int getDestinationAfterActionFromState(int s, int a) {
        if (isActionProbe[a] || attacksEndingInSourceForStates.get(s).contains(a)) {
            return s;
        }
        return finalS;
    }

    @Override
    public double getProbability(int s, int a, int s_) {
        int s_Saved = getDestinationAfterActionFromState(s, a);
        if (s_Saved == s_) {
            return 1.0;
        }
        return 0.0;
    }

    @Override
    public Iterator<Integer> getIteratorOverReachableStates(int s, int a) {
        return new SingleDestinationIterator(getDestinationAfterActionFromState(s, a));
    }

    private class SingleDestinationIterator implements Iterator<Integer> {

        private int destination;

        public SingleDestinationIterator(int destination) {
            this.destination = destination;
        }

        @Override
        public boolean hasNext() {
            return destination >= 0;
        }

        @Override
        public Integer next() {
            int dst = destination;
            destination = -1;
            return dst;
        }
    }
}
