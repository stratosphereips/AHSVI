package AHSVI;

import java.lang.IllegalArgumentException;
import java.util.*;
import java.util.stream.Stream;

import POMDPProblem.POMDPProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    private final double epsilon;

    private AlphaVectorValueFunction<Integer> lbFunction;
    private PointBasedValueFunction ubFunction;

    public HSVIAlgorithm(POMDPProblem pomdpProblem, double epsilon) {
        try {
            Cplex.get().setParam(IloCplex.IntParam.RootAlg, 2);
        } catch (IloException e) {
            e.printStackTrace();
        }
        if (pomdpProblem.initBelief == null) {
            throw new IllegalArgumentException("You have to specify initial belief");
        }
        this.pomdpProblem = pomdpProblem;
        this.epsilon = epsilon;
        this.initValueFunctions();
    }

    public double getLBValueInInitBelief() {
        return lbFunction.getValue(pomdpProblem.initBelief);
    }

    public double getUBValueInInitBelief() {
        return ubFunction.getValue(pomdpProblem.initBelief);
    }

    private void initValueFunctions() {
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

    private double[] nextBelief(double[] belief, int a, int o) {
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

    private AlphaVector<Integer> getAlphaDotProdArgMax(double[] belief) {
        return lbFunction.getDotProdArgMax(belief);
    }

    private double width(double[] belief) {
        return ubFunction.getValue(belief) - lbFunction.getValue(belief);
    }

    private boolean widthLargerThanEps(double[] belief) {
        return width(belief) > epsilon;
    }

    public void solve() {
        int iter = 0;
        double lbVal, ubVal, lastLbVal, lastUbVal;
        System.out.println("###########################################################################");
        System.out.println("###########################################################################");
        ++iter;
        lbVal = lbFunction.getValue(pomdpProblem.initBelief);
        ubVal = ubFunction.getValue(pomdpProblem.initBelief);
        System.out.println("Solve iteration: " + iter);
        System.out.println("LB in init belief: " + lbVal);
        System.out.println("UB in init belief: " + ubVal);
        lastLbVal = lbVal;
        lastUbVal = ubVal;
        while (widthLargerThanEps(pomdpProblem.initBelief)) {
            explore(pomdpProblem.initBelief, 0);

            System.out.println("###########################################################################");
            System.out.println("###########################################################################");
            ++iter;
            lbVal = lbFunction.getValue(pomdpProblem.initBelief);
            ubVal = ubFunction.getValue(pomdpProblem.initBelief);
            System.out.println("Solve iteration: " + iter);
            System.out.println("LB in init belief: " + lbVal);
            System.out.printf("Diff to last iteration: %.20f\n", (lbVal - lastLbVal));
            System.out.println("UB in init belief: " + ubVal);
            System.out.printf("Diff to last iteration: %.20f\n", (ubVal - lastUbVal));
            lastLbVal = lbVal;
            lastUbVal = ubVal;
        }
    }

    private void explore(double[] belief, int t) {
        if (width(belief) <= epsilon * Math.pow(pomdpProblem.discount, -t)) {// TODO float instability
            return;
        }
        double[] nextBelief = select(belief, t);
        if (nextBelief != null) {
            explore(nextBelief, t + 1);
        }

        updateLb(belief);
        updateUb(belief);
    }

    private double[] select(double[] belief, int t) {
        int bestA = 0;
        double valueOfBestA = computeQ(belief, 0);
        double value;
        if (pomdpProblem.getNumberOfActions() > 1) {
            for (int a = 1; a < pomdpProblem.getNumberOfActions(); ++a) {
                // compute lower QV
                value = computeQ(belief, a);
                if (value > valueOfBestA) {
                    bestA = a;
                    valueOfBestA = value;
                }
            }
        }

        // compute best observation
        double[] bestNextBelief = null;
        double[] nextBelief;
        double valueOfBestO = 0;
        double prb, excess;
        for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
            nextBelief = nextBelief(belief, bestA, o);
            if (nextBelief != null) {
                prb = pomdpProblem.getProbabilityOfObservationPlayingAction(o, belief, bestA); // TODO Fix this (it should take belief as arg)
                excess = width(nextBelief) - epsilon * Math.pow(pomdpProblem.discount, -(t + 1)); // TODO added * gamma^-t
                value = prb * excess;
                if (value > valueOfBestO) {
                    valueOfBestO = value;
                    bestNextBelief = nextBelief;
                }
            }
        }

//        System.out.println("Belief: " + Arrays.toString(belief));
//        System.out.println("Next belief: " + Arrays.toString(bestNextBelief));
        return bestNextBelief;

    }

    private double computeQ(double[] belief, int a) {
        double rewardsSum = 0;
        double observationsValuesSum = 0;
        double observationsValuesSubSum;
        double[] nextBel;
        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            rewardsSum += pomdpProblem.rewards[s][a] * belief[s];
            observationsValuesSubSum = 0;
            for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                    nextBel = nextBelief(belief, a, o);
                    if (nextBel != null) {
                        observationsValuesSubSum += pomdpProblem.actionProbabilities[s][a][s_] * pomdpProblem.observationProbabilities[s_][a][o] *
                                ubFunction.getValue(nextBel);
                    }
                }
            }
            observationsValuesSum += belief[s] * observationsValuesSubSum;
        }
        observationsValuesSum *= pomdpProblem.discount;
        return rewardsSum + observationsValuesSum;
    }

    private double computeHV(double[] belief) {
        // [paper 3.3]
        double maxQa = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            maxQa = Math.max(maxQa, computeQ(belief, a));
        }
        return maxQa;
    }

    private void updateUb(double[] belief) {
        ubFunction.addPoint(belief, computeHV(belief));
    }


    private void updateLb(double[] belief) {
        // [paper Alg 3]
        ArrayList<AlphaVector<Integer>> betasAo = new ArrayList<>(pomdpProblem.getNumberOfObservations());
        double[] betaVec;
        double[] maxBetaVec = null;
        double maxBetaVecValue = Double.NEGATIVE_INFINITY;
        double sumOs_, betaVecValue;
        int bestA = 0;
        AlphaVector<Integer> beta;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                betasAo.add(getAlphaDotProdArgMax(nextBelief(belief, a, o)));
            }
            betaVec = new double[belief.length];
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                sumOs_ = 0;
                for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                    beta = betasAo.get(o);
                    if (beta == null) {
                        continue;
                    }
                    for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                        sumOs_ += beta.vector[s_] *
                                pomdpProblem.observationProbabilities[s_][a][o] *
                                pomdpProblem.actionProbabilities[s][a][s_];
                    }
                }
                betaVec[s] = pomdpProblem.rewards[s][a] + pomdpProblem.discount * sumOs_;
            }
            betasAo.clear();
            if (lbFunction.contains(betaVec)) {
                continue;
            }
            betaVecValue = HelperFunctions.dotProd(betaVec, belief);
            if (betaVecValue > maxBetaVecValue) {
                maxBetaVecValue = betaVecValue;
                maxBetaVec = betaVec;
                bestA = a;
            }
        }
        if (maxBetaVec != null) {
            lbFunction.addVector(maxBetaVec, bestA);
        }
    }

}
