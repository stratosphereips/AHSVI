package POMDPProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class POMDPProblem {
    final List<String> indexToStateName;
    final Map<String, Integer> stateNameToIndex;
    final List<Double> initBelief;

    public POMDPProblem() {
        indexToStateName = new ArrayList<>();
        stateNameToIndex = new TreeMap<>();
        initBelief = new ArrayList<>();
    }

    public void addState(String name) {
        stateNameToIndex.put(name, indexToStateName.size());
        indexToStateName.add(name);
    }


}
