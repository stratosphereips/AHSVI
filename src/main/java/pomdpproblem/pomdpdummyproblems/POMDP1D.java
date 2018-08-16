package pomdpproblem.pomdpdummyproblems;

import pomdpproblem.POMDPProblem;

import java.util.ArrayList;
import java.util.HashMap;

public class POMDP1D implements POMDPDummyProblemI {
    @Override
    public POMDPProblem load() {
        // preamble
        double discount = 0.75;


        ArrayList<String> stateNames = new ArrayList<>();
        HashMap<String, Integer> stateNameToIndex = new HashMap<>();

        stateNames.add("left");
        stateNames.add("middle");
        stateNames.add("right");
        stateNames.add("goal");
        for (int i = 0; i < stateNames.size(); ++i) {
            stateNameToIndex.put(stateNames.get(i), i);
        }


        ArrayList<String> actionNames = new ArrayList<>();
        HashMap<String, Integer> actionNameToIndex = new HashMap<>();

        actionNames.add("w0");
        actionNames.add("e0");
        for (int i = 0; i < actionNames.size(); ++i) {
            actionNameToIndex.put(actionNames.get(i), i);
        }


        ArrayList<String> observationNames = new ArrayList<>();
        HashMap<String, Integer> observationNameToIndex = new HashMap<>();

        observationNames.add("nothing");
        observationNames.add("goal");
        for (int i = 0; i < observationNames.size(); ++i) {
            observationNameToIndex.put(observationNames.get(i), i);
        }


        // pomdp body
        double[][][] actionProbabilities =
                new double[actionNames.size()][stateNames.size()][stateNames.size()];

        actionProbabilities[0][0][0] = 1.0;
        actionProbabilities[0][0][1] = 0.0;
        actionProbabilities[0][0][2] = 0.0;
        actionProbabilities[0][0][3] = 0.0;

        actionProbabilities[0][1][0] = 1.0;
        actionProbabilities[0][1][1] = 0.0;
        actionProbabilities[0][1][2] = 0.0;
        actionProbabilities[0][1][3] = 0.0;

        actionProbabilities[0][2][0] = 0.0;
        actionProbabilities[0][2][1] = 0.0;
        actionProbabilities[0][2][2] = 0.0;
        actionProbabilities[0][2][3] = 1.0;

        actionProbabilities[0][3][0] = 1./3.;
        actionProbabilities[0][3][1] = 1./3.;
        actionProbabilities[0][3][2] = 1./3.;
        actionProbabilities[0][3][3] = 0.0;

        actionProbabilities[1][0][0] = 0.0;
        actionProbabilities[1][0][1] = 1.0;
        actionProbabilities[1][0][2] = 0.0;
        actionProbabilities[1][0][3] = 0.0;

        actionProbabilities[1][1][0] = 0.0;
        actionProbabilities[1][1][1] = 0.0;
        actionProbabilities[1][1][2] = 0.0;
        actionProbabilities[1][1][3] = 1.0;

        actionProbabilities[1][2][0] = 0.0;
        actionProbabilities[1][2][1] = 0.0;
        actionProbabilities[1][2][2] = 1.0;
        actionProbabilities[1][2][3] = 0.0;

        actionProbabilities[1][3][0] = 1./3.;
        actionProbabilities[1][3][1] = 1./3.;
        actionProbabilities[1][3][2] = 1./3.;
        actionProbabilities[1][3][3] = 0.0;


        double[][][] observationProbabilities =
                new double[actionNames.size()][stateNames.size()][observationNames.size()];

        observationProbabilities[0][0][0] = 1.0;
        observationProbabilities[0][0][1] = 0.0;
        observationProbabilities[0][1][0] = 1.0;
        observationProbabilities[0][1][1] = 0.0;
        observationProbabilities[0][2][0] = 1.0;
        observationProbabilities[0][2][1] = 0.0;
        observationProbabilities[0][3][0] = 0.0;
        observationProbabilities[0][3][1] = 1.0;

        observationProbabilities[1][0][0] = 1.0;
        observationProbabilities[1][0][1] = 0.0;
        observationProbabilities[1][1][0] = 1.0;
        observationProbabilities[1][1][1] = 0.0;
        observationProbabilities[1][2][0] = 1.0;
        observationProbabilities[1][2][1] = 0.0;
        observationProbabilities[1][3][0] = 0.0;
        observationProbabilities[1][3][1] = 1.0;


        double[][][][] rewards =
                new double[actionNames.size()][stateNames.size()][stateNames.size()][observationNames.size()];

        for (int a = 0; a < actionNames.size(); ++a) {
            for (int s = 0; s < stateNames.size(); ++s) {
                rewards[a][s][3][1] = 1.0;
            }
        }


        double[] initBelief = new double[stateNames.size()];

        // init belief was not in the original POMDP file
        initBelief[0] = 0.333333;
        initBelief[1] = 0.333333;
        initBelief[2] = 0.333333;
        initBelief[3] = 0.0;

        return new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, actionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }
}
