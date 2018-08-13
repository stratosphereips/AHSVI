package POMDPProblem.POMDPDummyProblems;

import POMDPProblem.POMDPProblem;

import java.util.ArrayList;
import java.util.HashMap;

public class POMDPTiger implements POMDPDummyProblemI {
    @Override
    public POMDPProblem load() {

        int statesCount = 2;
        int actionsCount = 3;
        int observationsCount = 2;

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
        discount = 0.9;

        stateNames.add("TL");
        stateNames.add("TR");
        for (int i = 0; i < stateNames.size(); ++i) {
            stateNameToIndex.put(stateNames.get(i), i);
        }

        actionNames.add("L");
        actionNames.add("OL");
        actionNames.add("OR");
        for (int i = 0; i < actionNames.size(); ++i) {
            actionNameToIndex.put(actionNames.get(i), i);
        }

        observationNames.add("TL");
        observationNames.add("TR");
        for (int i = 0; i < observationNames.size(); ++i) {
            observationNameToIndex.put(observationNames.get(i), i);
        }

        // pomdp body
        actionProbabilities[0][0][0] = 1.0;
        actionProbabilities[0][0][1] = 0.0;
        actionProbabilities[0][1][0] = 0.0;
        actionProbabilities[0][1][1] = 1.0;

        actionProbabilities[0][0][0] = 0.5;
        actionProbabilities[0][0][1] = 0.5;
        actionProbabilities[0][1][0] = 0.5;
        actionProbabilities[0][1][1] = 0.5;

        actionProbabilities[0][0][0] = 0.5;
        actionProbabilities[0][0][1] = 0.5;
        actionProbabilities[0][1][0] = 0.5;
        actionProbabilities[0][1][1] = 0.5;


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

        initBelief[0] = 0.5;
        initBelief[1] = 0.5;

        return new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, actionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }
}
