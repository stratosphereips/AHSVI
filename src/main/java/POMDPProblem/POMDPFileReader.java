package POMDPProblem;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class POMDPFileReader {

    private String pathToPOMDPFile;
    private boolean dummy;

    private POMDPProblem pomdpProblem;

    public POMDPFileReader(String fileName) {
        this(fileName, false);
    }

    public POMDPFileReader(String pathToPOMDPFile, boolean dummy) {
        this.pathToPOMDPFile = pathToPOMDPFile;
        this.dummy = dummy;
        pomdpProblem = null;
    }

    public void loadProblem() {
        System.out.println("Loading " + pathToPOMDPFile);
        if (dummy) {
            loadDummyPOMDP(pathToPOMDPFile);
        } else {
            loadPOMDPFile(pathToPOMDPFile);
        }
        System.out.println("Loading done");
    }

    public POMDPProblem getPomdpProblem() {
        return pomdpProblem;
    }

    private void loadPOMDPFile(String fileName) {
        System.out.println("NOPE");
        System.exit(10);
    }

    private void loadDummyPOMDP(String pathToPOMDPFile) {
        switch (Paths.get(pathToPOMDPFile).getFileName().toString()) {
            case "1s.POMDP":
                load1SProblem();
                break;
            case "1d.POMDP":
                load1DProblem();
                break;
            default:
                throw new IllegalArgumentException("No such dummy POMDP");
        }
    }

    private void load1SProblem() {

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

        // init belief was not in the original POMDP file
        initBelief[0] = 1.0;

        pomdpProblem = new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, actionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }

    private void load1DProblem() {
        System.out.println("Loading dummy 1d.POMDP");

        int statesCount = 4;
        int actionsCount = 2;
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
        discount = 0.75;

        stateNames.add("left");
        stateNames.add("middle");
        stateNames.add("right");
        stateNames.add("goal");
        for (int i = 0; i < stateNames.size(); ++i) {
            stateNameToIndex.put(stateNames.get(i), i);
        }

        actionNames.add("w0");
        actionNames.add("e0");
        for (int i = 0; i < actionNames.size(); ++i) {
            actionNameToIndex.put(actionNames.get(i), i);
        }

        observationNames.add("nothing");
        observationNames.add("goal");
        for (int i = 0; i < observationNames.size(); ++i) {
            observationNameToIndex.put(observationNames.get(i), i);
        }

        // pomdp body
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
        actionProbabilities[0][3][2] = 1. /3.;
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


        for (int a = 0; a < actionsCount; ++a) {
            for (int s = 0; s < statesCount; ++s) {
                rewards[a][s][3][1] = 1.0;
            }
        }

        // init belief was not in the original POMDP file
        initBelief[0] = 0.333333;
        initBelief[1] = 0.333333;
        initBelief[2] = 0.333333;
        initBelief[3] = 0.0;

        pomdpProblem = new POMDPProblem(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex, actionProbabilities,
                observationNames, observationNameToIndex, observationProbabilities,
                rewards, discount, initBelief);
    }
}
