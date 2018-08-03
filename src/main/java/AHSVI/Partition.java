package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;

import java.util.*;

/**
 * Created by wigos on 8.8.16.
 */
public class Partition {

    private final POMDPProblem pomdpProblem;

    public AlphaVectorValueFunction<Integer> lbFunction;
    public PointBasedValueFunction ubFunction;

    public Partition(POMDPProblem pomdpProblem) {
        this.pomdpProblem = pomdpProblem;
    }

    public void initValueFunctions() {
        lbFunction = initLowerBound();
        ubFunction = initUpperBound();
    }

    private AlphaVectorValueFunction initLowerBound() {
        //TODO is this k?
        AlphaVectorValueFunction lbF = new AlphaVectorValueFunction(pomdpProblem.getNumberOfStates());

        double minRsa, minRa;
        double R_ = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            minRsa = Double.POSITIVE_INFINITY;
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                if (pomdpProblem.initBelief[s] > 0) {
                    minRsa = Math.min(minRsa, pomdpProblem.rewards[s][a]);
                }
            }
            minRa = minRsa / (1 - pomdpProblem.discount);
            R_ = Math.max(R_, minRa);
        }

        double[] initAlpha = new double[pomdpProblem.getNumberOfStates()];
        Arrays.fill(initAlpha, R_);
        lbFunction.addVector(initAlpha); //TODO second argument to addVector?

        return lbF;
    }

    private PointBasedValueFunction initUpperBound() {
        PointBasedValueFunction ubF = new PointBasedValueFunction(pomdpProblem.getNumberOfStates());

        //TODO what is happening here
        // set ub with perfect-information setting
        for (int state = 0; state < setting.getNumberOfStates(); state++) {
            Pair<UserTypeI, Long> userTypeIIntegerPair = setting.indexToState.get(state);
            if (userTypeIIntegerPair != null) {
                long threshold = userTypeIIntegerPair.getRight();
                int thresholdIndex = setting.getDefendersThresholdActionInverse(threshold);

                bestValue = -1;
                for (int actionIndex = 0; actionIndex < setting.thresholds.size(); actionIndex++) {
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

        //TODO
        return ubF;
    }

    public double[] nextBelief(double[] belief, int a, int o) {
        // [paper 2.update b']
        double[] beliefNew = new double[belief.length];
        double normConstant = 0;
        for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                beliefNew[s_] += pomdpProblem.actionProbabilities[s][a][s_] * belief[s];
            }
            beliefNew[s_] *= pomdpProblem.observationProbabilities[s_][a][o];
            normConstant += beliefNew[s_];
        }
        for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
            beliefNew[s_] /= normConstant;
        }
        return beliefNew;
    }

}
