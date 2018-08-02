package POMDPProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class POMDPProblem {
    final List<String> indexToStateName;
    final Map<String, Integer> stateNameToIndex;
    final double[] initBelief;
    double discount;

    public POMDPProblem() {
        indexToStateName = new ArrayList<>();
        stateNameToIndex = new TreeMap<>();
        initBelief = null;
    }

    public void addState(String name) {
        stateNameToIndex.put(name, indexToStateName.size());
        indexToStateName.add(name);
    }

    public String indexToStateName(int i) {
        return indexToStateName.get(i);
    }

    public int stateNameToIndex(String name) {
        return stateNameToIndex.get(name);
    }
}
