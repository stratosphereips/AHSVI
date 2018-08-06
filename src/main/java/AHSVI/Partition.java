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
        int bestA = 0;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            minRsa = Double.POSITIVE_INFINITY;
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                if (pomdpProblem.initBelief[s] > 0) {
                    minRsa = Math.min(minRsa, pomdpProblem.rewards[s][a]);
                }
            }
            minRa = minRsa / (1 - pomdpProblem.discount);
            if (minRa > R_) {
                R_ = minRa;
                bestA = a;
            }
        }

        double[] initAlpha = new double[pomdpProblem.getNumberOfStates()];
        Arrays.fill(initAlpha, R_);
        lbFunction.addVector(initAlpha, bestA);

        return lbF;
    }

    private PointBasedValueFunction initUpperBound() {
        PointBasedValueFunction ubF = new PointBasedValueFunction(pomdpProblem.getNumberOfStates());

        //TODO what is happening here
        // set ub with perfect-information setting
        
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

    public AlphaVector<Integer> getAlphaDotProdArgMax(double[] belief) {
        return lbFunction.getDotProdArgMax(belief);
    }

    public AlphaVector<Integer> getAlphaDotProdArgMax(double[] belief, int a, int o) {
        return getAlphaDotProdArgMax(nextBelief(belief, a, o));
    }
}
