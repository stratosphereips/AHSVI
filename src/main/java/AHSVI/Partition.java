package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;

import java.util.*;

/**
 * Created by wigos on 8.8.16.
 */
public class Partition {

    private final POMDPProblem pomdpProblem;
    protected int leaderActions;

    protected double minValue = Double.NaN;
    protected double maxValue = Double.NaN;

    public AlphaVectorValueFunction<Integer> lbFunction;
    public PointBasedValueFunction ubFunction;

    public Partition(int leaderActions, POMDPProblem pomdpProblem) {
        this.leaderActions = leaderActions;
        this.pomdpProblem = pomdpProblem;
    }

    public void initValueFunctions() {
        lbFunction = new AlphaVectorValueFunction(pomdpProblem.getNumberOfStates());
        ubFunction = new PointBasedValueFunction(pomdpProblem.getNumberOfStates());

        Collection<Long> rewards = setting.thresholds.values();
        long minReward = Collections.min(rewards);
        long maxReward = Collections.max(rewards);
        double gamma = setting.discount;

        minValue = minReward / (1 - gamma);
        maxValue = maxReward / (1 - gamma);

        double[] minVector = new double[setting.getNumberOfStates()];
        for (int i = 0; i < minVector.length; i++) {
            minVector[i] = minValue;
        }

        // TODO: How to set initial lbFunction and ubFunction???
        // set lb best fixed action to play forever
//        lbFunction.addVector(minVector);

        double[] bestVector = new double[setting.getNumberOfStates()];
        double bestValue = -1;
        double bestAction = 0;
        for (int actionIndex = 0; actionIndex < setting.thresholds.size(); actionIndex++) {
            double valueOfCurrentAction = 0;
            double[] vectorOfCurrentAction = new double[setting.getNumberOfStates()];
            for (int state = 0; state < setting.getNumberOfStates(); state++) {
                if (setting.initBelief[state] > 0) {
                    Pair<UserTypeI, Long> userTypeIIntegerPair = setting.indexToState.get(state);
                    if (userTypeIIntegerPair != null) {
                        long threshold = userTypeIIntegerPair.getRight();
                        int thresholdIndex = setting.getDefendersThresholdActionInverse(threshold);
                        double v = userTypeIIntegerPair.getLeft().probabilityOfNotDetectingActionForThreshold(actionIndex, thresholdIndex, setting.IS_ADDITIVE);
//                        double v = userTypeIIntegerPair.isInCurb().getProbabilityOfNotDetectingNormalized(thresholdIndex, actionIndex, 0, setting.IS_ADDITIVE);
                        valueOfCurrentAction += setting.initBelief[state] * setting.getAttackerUtilityForAction(actionIndex) * v / (1 - setting.discount * v);
                        vectorOfCurrentAction[state] = setting.initBelief[state] * setting.getAttackerUtilityForAction(actionIndex) * v / (1 - setting.discount * v);
                    } else {
                        vectorOfCurrentAction[state] = 0;
                    }
                }
            }

            if (valueOfCurrentAction > bestValue) {
                bestValue = valueOfCurrentAction;
                bestVector = vectorOfCurrentAction;
//                bestAction = actionIndex;
            }
            lbFunction.addVector(vectorOfCurrentAction, actionIndex);
        }
        lbFunction.addVector(bestVector);


        // set ub with perfect-information setting
        for (int state = 0; state < setting.getNumberOfStates(); state++) {
            Pair<UserTypeI, Long> userTypeIIntegerPair = setting.indexToState.get(state);
            if (userTypeIIntegerPair != null) {
                long threshold = userTypeIIntegerPair.getRight();
                int thresholdIndex = setting.getDefendersThresholdActionInverse(threshold);

                bestValue = -1;
                for (int actionIndex = 0; actionIndex < setting.thresholds.size(); actionIndex++) {
//            for (Integer action : setting.actions) {
                    double v = userTypeIIntegerPair.getLeft().probabilityOfNotDetectingActionForThreshold(actionIndex, thresholdIndex, setting.IS_ADDITIVE);
                    double expUtility = setting.getAttackerUtilityForAction(actionIndex) * v / (1 - setting.discount * v);
                    if (expUtility > bestValue) {
                        bestValue = expUtility;
                    }
                }
            } else {
                bestValue = 0;
            }

            double[] maxBelief = new double[minVector.length];
            maxBelief[state] = 1.0;
            ubFunction.addPoint(maxBelief, bestValue);
        }


//        for ( int state = 0; state < setting.indexToState.size(); state++ ) {
//            double[] maxBelief = new double[minVector.length];
//            maxBelief[state] = 1.0;
//
//            ubFunction.addPoint(maxBelief, maxValue);
//        }
    }

    public double[] nextBelief(double[] belief, int actionInd, int observationInd) {
        double[] newBel = new double[belief.length];
        double sum = 0;
        for (int i = 0; i < belief.length; i++) {
            if (belief[i] < Config.ZERO) {
                continue;
            }
            cz.agents.deceptiongame.dynprog.auxiliary.Pair<UserTypeI, Long> userTypeIIntegerPair = setting.indexToState.get(i);
            if (userTypeIIntegerPair != null) {
                // todo new
                int thresholdIndex = setting.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight());
//            double prbOfNotDetecting = userTypeIIntegerPair.isInCurb().getProbabilityOfNotDetectingNormalized(userTypeIIntegerPair.getAttError(), actionInd, observationInd, setting.IS_ADDITIVE);
                double prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(thresholdIndex, actionInd, observationInd, setting.IS_ADDITIVE);
                newBel[i] = belief[i] * prbOfNotDetecting * userTypeIIntegerPair.getLeft().getProbabilityOfObservationToNextStep(observationInd);
                if (newBel[i] < Config.ZERO) {
                    newBel[i] = 0;
                }
                sum += newBel[i];
            }
            // todo new

            // todo old
//            if (setting.IS_ADDITIVE && action + observation <= userTypeIIntegerPair.getAttError()) {
//                newBel[i] = belief[i] * userTypeIIntegerPair.isInCurb().getProbabilityOfObservation(observation);
//                if ( newBel[i] < Config.ZERO ) {
//                    newBel[i] = 0;
//                }
//                sum += newBel[i];
//            } else if ( !setting.IS_ADDITIVE && action <= userTypeIIntegerPair.getAttError()) {
//                newBel[i] = belief[i] * userTypeIIntegerPair.isInCurb().getProbabilityOfObservation(observation);
//                if ( newBel[i] < Config.ZERO ) {
//                    newBel[i] = 0;
//                }
//                sum += newBel[i];
//            } else {
//                newBel[i] = 0;
//            }
            // todo old
        }

        // normalize

        if (sum < Config.ZERO || Double.isNaN(sum)) return null;

        for (int i = 0; i < newBel.length; i++) {
            newBel[i] /= sum;
        }

        double total = Arrays.stream(newBel).sum();
        assert total <= 1 + Config.ZERO && total >= 0 - Config.ZERO;
        return newBel;
    }

}
