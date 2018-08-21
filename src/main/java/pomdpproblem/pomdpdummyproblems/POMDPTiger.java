package pomdpproblem.pomdpdummyproblems;

import pomdpproblem.POMDPProblem;

import java.util.ArrayList;
import java.util.HashMap;

public class POMDPTiger implements POMDPDummyProblemI {
    @Override
    public POMDPProblem load() {
        // preamble
        double discount = 0.9;


        ArrayList<String> stateNames = new ArrayList<>();
        HashMap<String, Integer> stateNameToIndex = new HashMap<>();

        stateNames.add("TL");
        stateNames.add("TR");
        for (int i = 0; i < stateNames.size(); ++i) {
            stateNameToIndex.put(stateNames.get(i), i);
        }


        ArrayList<String> actionNames = new ArrayList<>();
        HashMap<String, Integer> actionNameToIndex = new HashMap<>();

        actionNames.add("L");
        actionNames.add("OL");
        actionNames.add("OR");
        for (int i = 0; i < actionNames.size(); ++i) {
            actionNameToIndex.put(actionNames.get(i), i);
        }


        ArrayList<String> observationNames = new ArrayList<>();
        HashMap<String, Integer> observationNameToIndex = new HashMap<>();

        observationNames.add("TL");
        observationNames.add("TR");
        for (int i = 0; i < observationNames.size(); ++i) {
            observationNameToIndex.put(observationNames.get(i), i);
        }


        // pomdp body
        double[][][] transitionProbabilities =
                new double[actionNames.size()][stateNames.size()][stateNames.size()];

        transitionProbabilities[0][0][0] = 1.0;
        transitionProbabilities[0][0][1] = 0.0;
        transitionProbabilities[0][1][0] = 0.0;
        transitionProbabilities[0][1][1] = 1.0;

        transitionProbabilities[1][0][0] = 0.5;
        transitionProbabilities[1][0][1] = 0.5;
        transitionProbabilities[1][1][0] = 0.5;
        transitionProbabilities[1][1][1] = 0.5;

        transitionProbabilities[2][0][0] = 0.5;
        transitionProbabilities[2][0][1] = 0.5;
        transitionProbabilities[2][1][0] = 0.5;
        transitionProbabilities[2][1][1] = 0.5;


        double[][][] observationProbabilities =
                new double[actionNames.size()][stateNames.size()][observationNames.size()];

        observationProbabilities[0][0][0] = 0.85;
        observationProbabilities[0][0][1] = 0.15;
        observationProbabilities[0][1][0] = 0.15;
        observationProbabilities[0][1][1] = 0.85;

        observationProbabilities[1][0][0] = 0.5;
        observationProbabilities[1][0][1] = 0.5;
        observationProbabilities[1][1][0] = 0.5;
        observationProbabilities[1][1][1] = 0.5;

        observationProbabilities[2][0][0] = 0.5;
        observationProbabilities[2][0][1] = 0.5;
        observationProbabilities[2][1][0] = 0.5;
        observationProbabilities[2][1][1] = 0.5;


        double[][][][] rewards =
                new double[actionNames.size()][stateNames.size()][stateNames.size()][observationNames.size()];

        for (int o = 0; o < observationNames.size(); ++o) {
            rewards[0][0][0][o] = -1;
            rewards[0][0][1][o] = -1;
            rewards[0][1][0][o] = -1;
            rewards[0][1][1][o] = -1;

            rewards[1][0][0][o] = -100;
            rewards[1][0][1][o] = +10;
            rewards[1][1][0][o] = -100;
            rewards[1][1][1][o] = +10;

            rewards[2][0][0][o] = +10;
            rewards[2][0][1][o] = -100;
            rewards[2][1][0][o] = +10;
            rewards[2][1][1][o] = -100;
        }


        double[] initBelief = new double[stateNames.size()];

        initBelief[0] = 0.5;
        initBelief[1] = 0.5;

        return new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, transitionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }
}
