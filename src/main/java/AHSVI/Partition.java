package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;

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
        LBInitializer lbInit = new LBInitializer(pomdpProblem);
        lbInit.computeInitialLB();
        return lbInit.getLB();
    }

    private PointBasedValueFunction initUpperBound() {
        UBInitializer ubInit = new UBInitializer(pomdpProblem);
        ubInit.computeInitialUB();
        return ubInit.getUB();
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
