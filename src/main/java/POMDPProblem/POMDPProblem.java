package main.java.POMDPProblem;

import java.util.*;

public class POMDPProblem {
    public final ArrayList<String> stateNames;
    public final HashMap<String, Integer> stateNameToIndex;
    public final ArrayList<String> actionNames;
    public final HashMap<String, Integer> actionNameToIndex;
    public final double[][][] actionProbabilities; // probability p of getting from start-state s_ to end-state s_ playing
    //                                         action a ... p = actionProbabilities[s][a][s_]
    public final ArrayList<String> observationNames;
    public final HashMap<String, Integer> observationNameToIndex;
    public final double[][][] observationProbabilities; // probability p of seeing an observation o playing action a getting to
    //                                              end-state s_ ... p = observationProbabilities[s_][a][o]
    public final double[][] rewards; // reward r for playing action a in state s ... r = rewards[s][a]
    public final double discount;
    public final double[] initBelief;
    public final boolean minimize;

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] actionProbabilities, // ap[a][s][s_]
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities, //op[a][s_][o]
                        double[][][][] rewards, //r[a][s][s_][o]
                        double discount,
                        boolean minimize,
                        double[] initBelief) {
        this.stateNames = new ArrayList<>(stateNames);
        this.stateNameToIndex = stateNameToIndex;
        this.actionNames = new ArrayList<>(actionNames);
        this.actionNameToIndex = actionNameToIndex;
        this.actionProbabilities = transformActionProbabilitiesToHSVI(actionProbabilities);
        this.observationNames = new ArrayList<>(observationNames);
        this.observationNameToIndex = observationNameToIndex;
        this.observationProbabilities = transformObservationProbabilitiesToHSVI(observationProbabilities);
        this.rewards = transformRewardsToHSVI(rewards);
        this.discount = discount;
        this.minimize = minimize;
        this.initBelief = initBelief;

    }

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] actionProbabilities,
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities,
                        double[][][][] rewards,
                        double discount,
                        boolean minimize) {
        this(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex,
                actionProbabilities,
                observationNames, observationNameToIndex,
                observationProbabilities,
                rewards,
                discount,
                minimize,
                null);
    }

    public int getNumberOfStates() {
        return stateNames.size();
    }

    public double getProbabilityOfObservationPlayingAction(int a, int o) {
        double probSum = 0;
        for (int s_ = 0; s_ < stateNames.size(); ++s_) {
            probSum += observationProbabilities[a][s_][o];
        }
        return probSum;
    }

    private double[][][] transformActionProbabilitiesToHSVI(double[][][] actionProbabilities) {
        int statesCount = actionProbabilities[0].length;
        int actionsCount = actionProbabilities.length;
        double[][][] actionProbabilitiesTransformed = new double[statesCount][actionsCount][statesCount];
        for (int a = 0; a < actionsCount; ++a) {
            for (int s = 0; s < statesCount; ++s) {
                for (int s_ = 0; s_ < statesCount; ++s_) {
                    actionProbabilitiesTransformed[s][a][s_] = actionProbabilities[a][s][s_];
                }
            }
        }
        return actionProbabilitiesTransformed;
    }

    private double[][][] transformObservationProbabilitiesToHSVI(double[][][] observationProbabilities) {
        int actionsCount = observationProbabilities[0].length;
        int statesCount = observationProbabilities.length;
        int observationsCount = observationProbabilities[0][0].length;
        double[][][] observationProbabilitiesTransformed = new double[actionsCount][statesCount][observationsCount];
        for (int a = 0; a < actionsCount; ++a) {
            for (int s = 0; s < statesCount; ++s) {
                for (int o = 0; o < statesCount; ++o) {
                    observationProbabilitiesTransformed[s][a][o] = observationProbabilities[a][s][o];
                }
            }
        }
        return observationProbabilitiesTransformed;
    }

    private double[][] transformRewardsToHSVI(double[][][][] rewards) {
        int statesCount = rewards[0].length;
        int actionsCount = rewards.length;
        int observationsCount = rewards[0][0][0].length;
        double[][] rewardsTransformed = new double[statesCount][actionsCount];
        for (int a = 0; a < actionsCount; ++a) {
            for (int s = 0; s < statesCount; ++s) {
                for (int s_ = 0; s_ < statesCount; ++s_) {
                    for (int o = 0; o < observationsCount; ++o) {
                        rewardsTransformed[s][a] += actionProbabilities[s][a][s_] * observationProbabilities[s_][a][o] *
                                rewards[a][s][s_][o];
                    }
                }
            }
        }
        return rewardsTransformed;
    }
}
