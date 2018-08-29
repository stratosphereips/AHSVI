package pomdpproblem;

import hsvi.Config;

import java.util.*;

public class POMDPProblem {
    public final ArrayList<String> stateNames;
    public final HashMap<String, Integer> stateNameToIndex;
    public final ArrayList<String> actionNames;
    public final HashMap<String, Integer> actionNameToIndex;
    public final double[][][] transitionProbabilities; // probability p of getting from start-state s_ to end-state s_ playing
    //                                         action a ... p = transitionProbabilities[s][a][s_]
    public final ArrayList<String> observationNames;
    public final HashMap<String, Integer> observationNameToIndex;
    public final double[][][] observationProbabilities; // probability p of seeing an observation o playing action a getting to
    //                                              end-state s_ ... p = observationProbabilities[s_][a][o]
    public final double[][] rewards; // reward r for playing action a in state s ... r = rewards[s][a]
    public final double discount;
    public double[] initBelief;
    public final boolean minimize;

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] transitionProbabilities, // ap[a][s][s_]
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities, //op[a][s_][o]
                        double[][][][] rewards, //r[a][s][s_][o]
                        double discount,
                        double[] initBelief,
                        boolean minimize) {
        this.stateNames = new ArrayList<>(stateNames);
        this.actionNames = new ArrayList<>(actionNames);
        this.observationNames = new ArrayList<>(observationNames);

        this.stateNameToIndex = stateNameToIndex;
        this.actionNameToIndex = actionNameToIndex;
        this.transitionProbabilities = transformTransitionProbabilitiesToHSVI(transitionProbabilities);
        this.observationNameToIndex = observationNameToIndex;
        this.observationProbabilities = transformObservationProbabilitiesToHSVI(observationProbabilities);
        this.rewards = transformRewardsToHSVI(rewards);
        this.discount = discount;
        this.initBelief = initBelief;
        this.minimize = minimize;

        assert areTransitionProbabilitiesCorrect(): "Transition probabilities are probably incorrect";
        assert areObservationProbabilitiesCorrect(): "Observation probabilities are probably incorrect";
        assert 0 <= discount && discount <= 1: "Discount must be between 0 and 1";

    }

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] transitionProbabilities,
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities,
                        double[][][][] rewards,
                        double discount,
                        double[] initBelief) {
        this(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex,
                transitionProbabilities,
                observationNames, observationNameToIndex,
                observationProbabilities,
                rewards,
                discount,
                initBelief,
                false);
    }

    public POMDPProblem(List<String> stateNames, HashMap<String, Integer> stateNameToIndex,
                        List<String> actionNames, HashMap<String, Integer> actionNameToIndex,
                        double[][][] transitionProbabilities,
                        List<String> observationNames, HashMap<String, Integer> observationNameToIndex,
                        double[][][] observationProbabilities,
                        double[][][][] rewards,
                        double discount) {
        this(stateNames, stateNameToIndex,
                actionNames, actionNameToIndex,
                transitionProbabilities,
                observationNames, observationNameToIndex,
                observationProbabilities,
                rewards,
                discount,
                null,
                false);
    }

    public int getNumberOfStates() {
        return stateNames.size();
    }

    public int getNumberOfActions() {
        return actionNames.size();
    }

    public int getNumberOfObservations() {
        return observationNames.size();
    }

    public boolean areTransitionProbabilitiesCorrect() {
        double probSum;
        for (int s = 0; s < getNumberOfStates(); ++s) {
            for (int a = 0; a < getNumberOfActions(); ++a) {
                probSum = 0;
                for (int s_ = 0; s_ < getNumberOfStates(); ++s_) {
                    probSum += transitionProbabilities[s][a][s_];
                }
                if (Math.abs(probSum - 1) > Config.ZERO) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areObservationProbabilitiesCorrect() {
        double probSum;
        for (int s_ = 0; s_ < getNumberOfStates(); ++s_) {
            for (int a = 0; a < getNumberOfActions(); ++a) {
                probSum = 0;
                for (int o = 0; o < getNumberOfObservations(); ++o) {
                    probSum += observationProbabilities[s_][a][o];
                }
                if (Math.abs(probSum - 1) > Config.ZERO) {
                    return false;
                }
            }
        }
        return true;
    }

    public double getProbabilityOfObservationPlayingAction(int o, double[] belief, int a) {
        double probSum = 0;
        double probSubSum;
        for (int s = 0; s < getNumberOfStates(); ++s) {
            probSubSum = 0;
            for (int s_ = 0; s_ < getNumberOfStates(); ++s_) {
                probSubSum += transitionProbabilities[s][a][s_] * observationProbabilities[s_][a][o];
            }
            probSum += belief[s] * probSubSum;
        }
        return probSum;
    }

    private double[][][] transformTransitionProbabilitiesToHSVI(double[][][] transitionProbabilities) {
        double[][][] transitionProbabilitiesTransformed =
                new double[getNumberOfStates()][getNumberOfActions()][getNumberOfStates()];
        for (int a = 0; a < getNumberOfActions(); ++a) {
            for (int s = 0; s < getNumberOfStates(); ++s) {
                for (int s_ = 0; s_ < getNumberOfStates(); ++s_) {
                    transitionProbabilitiesTransformed[s][a][s_] = transitionProbabilities[a][s][s_];
                }
            }
        }
        return transitionProbabilitiesTransformed;
    }

    private double[][][] transformObservationProbabilitiesToHSVI(double[][][] observationProbabilities) {
        double[][][] observationProbabilitiesTransformed =
                new double[getNumberOfStates()][getNumberOfActions()][getNumberOfObservations()];
        for (int a = 0; a < getNumberOfActions(); ++a) {
            for (int s_ = 0; s_ < getNumberOfStates(); ++s_) {
                for (int o = 0; o < getNumberOfObservations(); ++o) {
                    observationProbabilitiesTransformed[s_][a][o] = observationProbabilities[a][s_][o];
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
                        rewardsTransformed[s][a] += transitionProbabilities[s][a][s_] * observationProbabilities[s_][a][o] *
                                rewards[a][s][s_][o];
                    }
                }
            }
        }
        return rewardsTransformed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("pomdpproblem");
        sb.append("\\\n\tStates:{");
        for (String s : stateNames) {
            sb.append(s);
            sb.append(',');
        }
        sb.append('}');
        sb.append("\\\n\tActions:{");
        for (String a : actionNames) {
            sb.append(a);
            sb.append(',');
        }
        sb.append('}');
        sb.append("\\\n\tObservations:{");
        for (String o : observationNames) {
            sb.append(o);
            sb.append(',');
        }
        sb.append('}');

        sb.append("\\\n\tTransition probabilities");
        sb.append("\\\n\t\tTODO"); //TODO pomdpproblem toString()

        return sb.toString();
    }
}
