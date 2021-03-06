package hsvi;

import java.util.*;

import helpers.HelperFunctions;
import hsvi.bounds.*;
import pomdpproblem.POMDPProblem;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    protected final POMDPProblem pomdpProblem;
    protected final double epsilon;

    protected LowerBound lbFunction;
    protected UpperBound ubFunction;

    public HSVIAlgorithm(POMDPProblem pomdpProblem, double epsilon) {
        this.pomdpProblem = pomdpProblem;
        this.epsilon = epsilon;
        this.initValueFunctions();
    }

    public double getLBValueInBelief(double[] belief) {
        return lbFunction.getValue(belief);
    }

    public LowerBound getLbFunction() {
        return lbFunction;
    }

    public UpperBound getUbFunction() {
        return ubFunction;
    }

    public double getLBValueInInitBelief() {
        return getLBValueInBelief(pomdpProblem.initBelief);
    }

    public double getUBValueInBelief(double[] belief) {
        return ubFunction.getValue(belief);
    }

    public double getUBValueInInitBelief() {
        return getUBValueInBelief(pomdpProblem.initBelief);
    }

    private void initValueFunctions() {
        lbFunction = initLowerBound();
        ubFunction = initUpperBound();
    }

    private LowerBound initLowerBound() {
        LBInitializer lbInit = new LBInitializer(pomdpProblem);
        lbInit.computeInitialLB();
        LBAlphaVector initialLBAlphaVector = lbInit.getInitialAlphaVector();

        LowerBound lb = new LowerBound(pomdpProblem.getNumberOfStates());
        lb.addAlphaVector(initialLBAlphaVector);
        return lb;
    }

    private UpperBound initUpperBound() {
        MDPUBInitializer ubInit = new MDPUBInitializer(pomdpProblem);
        ubInit.computeInitialUB();
        double[] initialUBExtremePointsValue = ubInit.getInitialUbExtremePointsValues();

        UpperBound up;
        //up = new CplexLPUpperBound(pomdpProblem.getNumberOfStates(), initialUBExtremePointsValue);
        up = new SawtoothUpperBound(pomdpProblem.getNumberOfStates(), initialUBExtremePointsValue);
        return up;
    }

    private double[] nextBelief(double[] belief, int a, int o) {
        // [paper 2.update b']
        double[] beliefNew = new double[belief.length];
        double normConstant = 0;
        for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
            if (pomdpProblem.observationProbabilities[s_][a][o] < Config.ZERO) {
                continue;
            }
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

    private LBAlphaVector getAlphaDotProdArgMax(double[] belief) {
        return lbFunction.getDotProdArgMax(belief);
    }

    private double width(double[] belief) {
        return ubFunction.getValue(belief) - lbFunction.getValue(belief);
    }

    private boolean widthLargerThanEps(double[] belief) {
        return width(belief) > epsilon;
    }

    public void solve() {
        long timeStarted = System.currentTimeMillis();
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
            explore(pomdpProblem.initBelief, 0, iter);

            System.out.println("###########################################################################");
            System.out.println("###########################################################################");
            ++iter;
            lbVal = lbFunction.getValue(pomdpProblem.initBelief);
            ubVal = ubFunction.getValue(pomdpProblem.initBelief);
            System.out.println("Solve iteration: " + iter);
            System.out.println("LB in init belief: " + lbVal);
            System.out.printf(" ----- Diff to last iteration: %.20f\n", (lbVal - lastLbVal));
            System.out.println("LB size: " + lbFunction.getAlphaVectors().size());
            System.out.println("UB in init belief: " + ubVal);
            System.out.printf(" ----- Diff to last iteration: %.20f\n", (ubVal - lastUbVal));
            System.out.println("UB size: " + ubFunction.getPoints().size());
            System.out.println("Running time so far [s]: " + ((System.currentTimeMillis() - timeStarted) / 1000));
            assert ubVal <= lastUbVal;
            assert lbVal >= lastLbVal;
            System.out.println("===========================================================================");

            lastLbVal = lbVal;
            lastUbVal = ubVal;
        }
    }

    protected boolean exploreEndingCondition(double[] belief, int t, int iteration) {
        return width(belief) <= epsilon * Math.pow(pomdpProblem.discount, -t);
    }

    protected void explore(double[] belief, int t, int iteration) {
        if (exploreEndingCondition(belief, t, iteration)) {// TODO float instability
            return;
        }
        double[] nextBelief = select(belief, t);
        if (nextBelief != null) {
            explore(nextBelief, t + 1, iteration);
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
        double valueOfBestO = 0; // TODO Double.NEGATIVE_INFINITY; why 0?
        double prb, excess;
        for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
            nextBelief = nextBelief(belief, bestA, o);
            if (nextBelief != null) {
                prb = pomdpProblem.getProbabilityOfObservationPlayingAction(o, belief, bestA);
                excess = width(nextBelief) - epsilon * Math.pow(pomdpProblem.discount, -(t + 1));
                value = prb * excess;
                if (value > valueOfBestO) {
                    valueOfBestO = value;
                    bestNextBelief = nextBelief;
                }
            }
        }
        return bestNextBelief;

    }

    private double computeQ(double[] belief, int a) {
        double rewardsSum = 0;
        double observationsValuesSum = 0;
        double observationsValuesSubSum;
        double[] nextBel;
        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            if (belief[s] < Config.ZERO) {
                continue;
            }
            rewardsSum += pomdpProblem.rewards[s][a] * belief[s];
            observationsValuesSubSum = 0;
            for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                if (pomdpProblem.actionProbabilities[s][a][s_] < Config.ZERO) {
                    continue;
                }
                for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                    nextBel = nextBelief(belief, a, o);
                    if (nextBel != null &&
                            pomdpProblem.observationProbabilities[s_][a][o] > Config.ZERO) {
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
        double p = computeHV(belief);
        ubFunction.addPoint(belief, p);
    }


    private void updateLb(double[] belief) {
        // [paper Alg 3]
        ArrayList<LBAlphaVector> betasAo = new ArrayList<>(pomdpProblem.getNumberOfObservations());
        double[] betaVec;
        double[] maxBetaVec = null;
        double maxBetaVecValue = Double.NEGATIVE_INFINITY;
        double sumOs_, betaVecValue;
        int bestA = 0;
        LBAlphaVector beta;
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
