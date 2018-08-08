package AHSVI;

import POMDPProblem.POMDPProblem;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

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
        System.out.println("LB value in initial belief: " + lbFunction.getValue(pomdpProblem.initBelief)); // TODO print
        ubFunction = initUpperBound();
        System.out.println("UB value in initial belief: " + ubFunction.getValue(pomdpProblem.initBelief)); // TODO print
        System.out.println(ubFunction);
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
        if (normConstant < Config.ZERO) {
            return null;
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
