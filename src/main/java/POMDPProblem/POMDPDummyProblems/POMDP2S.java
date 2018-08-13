package POMDPProblem.POMDPDummyProblems;

import POMDPProblem.POMDPProblem;

import java.util.ArrayList;
import java.util.HashMap;

public class POMDP2S implements POMDPDummyProblemI {
    @Override
    public POMDPProblem load() {

        int statesCount = 2;
        int actionsCount = 1;
        int observationsCount = 1;

        ArrayList<String> stateNames = new ArrayList<>();
        HashMap<String, Integer> stateNameToIndex = new HashMap<>();
        ArrayList<String> actionNames = new ArrayList<>();
        HashMap<String, Integer> actionNameToIndex = new HashMap<>();
        double[][][] actionProbabilities = new double[actionsCount][statesCount][statesCount];
        ArrayList<String> observationNames = new ArrayList<>();
        HashMap<String, Integer> observationNameToIndex = new HashMap<>();
        double[][][] observationProbabilities = new double[actionsCount][statesCount][observationsCount];
        double[][][][] rewards = new double[actionsCount][statesCount][statesCount][observationsCount];
        double discount;
        double[] initBelief = new double[statesCount];

        // preamble
        discount = 0.75;

        stateNames.add("s");
        stateNames.add("v");
        for (int i = 0; i < stateNames.size(); ++i) {
            stateNameToIndex.put(stateNames.get(i), i);
        }

        actionNames.add("a");
        for (int i = 0; i < actionNames.size(); ++i) {
            actionNameToIndex.put(actionNames.get(i), i);
        }

        observationNames.add("nothing");
        for (int i = 0; i < observationNames.size(); ++i) {
            observationNameToIndex.put(observationNames.get(i), i);
        }

        // pomdp body
        actionProbabilities[0][0][0] = 0.0;
        actionProbabilities[0][0][1] = 1.0;
        actionProbabilities[0][1][0] = 1.0;
        actionProbabilities[0][1][1] = 0.0;


        observationProbabilities[0][0][0] = 1.0;
        observationProbabilities[0][1][0] = 1.0;

        rewards[0][0][1][0] = 1.0;
        rewards[0][1][0][0] = 0.0;

        initBelief[0] = 1.0;

        return new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, actionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }
}
