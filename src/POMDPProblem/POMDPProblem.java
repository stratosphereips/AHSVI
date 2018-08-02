package POMDPProblem;

import java.util.*;

public class POMDPProblem {
    final ArrayList<String> stateNames;
    final HashMap<String, Integer> stateNameToIndex;
    final ArrayList<String> actionNames;
    final HashMap<String, Integer> actionNameToIndex;
    final double[][][] actionProbabilities; // probability p of getting from start-state ss to end-state es ...
    //                                         p = actionProbabilities[a][ss][es]
    final ArrayList<String> observationNames;
    final HashMap<String, Integer> observationNameToIndex;
    final double[][][] observationProbabilities; // probability p of seeing an observation o playing action a getting to
    //                                              end-state s ... p = observationProbabilities[a][s][o]
    final double discount;
    final double[] initBelief;

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] actionProbabilities,
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities,
                        double discount,
                        double[] initBelief) {
        this.stateNames = new ArrayList<>(stateNames);
        this.stateNameToIndex = stateNameToIndex;
        this.actionNames = new ArrayList<>(actionNames);
        this.actionNameToIndex = actionNameToIndex;
        this.actionProbabilities = actionProbabilities;
        this.observationNames = new ArrayList<>(observationNames);
        this.observationNameToIndex = observationNameToIndex;
        this.observationProbabilities = observationProbabilities;
        this.discount = discount;
        this.initBelief = initBelief;

    }

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] actionProbabilities,
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities,
                        double discount) {
        this(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex,
                actionProbabilities,
                observationNames, observationNameToIndex,
                observationProbabilities,
                discount,
                null);
    }
}
