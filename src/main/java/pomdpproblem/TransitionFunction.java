package pomdpproblem;

import java.util.Iterator;

public interface TransitionFunction {

    double getProbability(int s, int a, int s_);

    Iterator<Integer> getIteratorOverReachableStates(int s, int a);
}
