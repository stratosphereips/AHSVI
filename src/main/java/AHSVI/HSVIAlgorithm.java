package main.java.AHSVI;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;

import main.java.POMDPProblem.POMDPProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    private final double epsilon;
    public Partition partition; //TODO are there more than one partitions?

    public double finalUtilityLB;
    public double finalUtilityUB;
    public double minLB, minUB;

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
        this.partition = new Partition(pomdpProblem);
        this.partition.initValueFunctions();
    }

    public void solve(Partition initialPartition, double epsilon) throws IloException {
        while (widthLargerThanEps(initialPartition, pomdpProblem.initBelief)) {
            explore(initialPartition, pomdpProblem.initBelief, 0);
        }

        /*
        while (true) {
            minLB = this.partition.lbFunction.getValue(pomdpProblem.initBelief);
            minUB = this.partition.ubFunction.getValue(pomdpProblem.initBelief);

            if ((minUB - minLB) / minUB < epsilon / 100) {
                break;
            }

            ePrime = epsilon / 100d; //TODO why epsilon / 100

            explore(initialPartition, pomdpProblem.initBelief, ePrime, 0);
        }
        */

        this.finalUtilityLB = partition.lbFunction.getValue(pomdpProblem.initBelief);
        this.finalUtilityUB = partition.ubFunction.getValue(pomdpProblem.initBelief);

    }

    private boolean widthLargerThanEps(Partition partition, double[] belief) {
        return width(partition, belief) > epsilon;
    }

    private void explore(Partition partition, double[] belief, int t) throws IloException {
        if (width(partition, belief) <= epsilon * Math.pow(pomdpProblem.discount, -t)) {
            return;
        }
        Triplet<Integer, Integer, double[]> tripletAOBelief = select(partition, belief);
        if (tripletAOBelief != null) {
            explore(partition, tripletAOBelief.getThird(), t + 1);
        }

        updateLb(partition, belief);
        updateUb(partition, belief);
    }

    private Triplet<Integer, Integer, double[]> select(Partition partition, double[] belief) {
        Triplet<Integer, Integer, double[]> best = null;

        int bestA = 0;
        double valueOfBestA = computeQ(belief, 0);
        double value;
        int actionsCount = pomdpProblem.actionNames.size();
        if (actionsCount > 1) {
            for (int a = 1; a < actionsCount; ++a) {
                // compute lower QV
                value = computeQ(belief, a);
                if (value > valueOfBestA) {
                    bestA = a;
                    valueOfBestA = value;
                }
            }
        }

        // compute best observation
        int bestO = 0;
        double valueOfBestO = 0;
        int observationsCount = pomdpProblem.observationNames.size();
        double prb, excess;
        for (int o = 0; o < observationsCount; ++o) {
            double[] nextBelief = partition.nextBelief(belief, bestA, o);
            if (nextBelief != null) {
                prb = pomdpProblem.getProbabilityOfObservationPlayingAction(bestA, o);
                excess = width(partition, nextBelief) - epsilon;
                value = prb * excess;
                if (value > valueOfBestO) {
                    bestO = o;
                    valueOfBestO = value;
                }
            }
        }

        double[] nextBel = partition.nextBelief(belief, bestA, bestO);
        if (valueOfBestO > 0) {
            best = new Triplet<>(bestA, bestO, nextBel);
        }

        return best;

    }

    private double computeQ(double[] belief, int a) {
        double rewardsSum = 0;
        double observationsValuesSum = 0;
        double observationsValuesSubSum;
        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            rewardsSum += pomdpProblem.rewards[s][a];
            observationsValuesSubSum = 0;
            for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                    observationsValuesSubSum += pomdpProblem.actionProbabilities[s][a][s_] *
                            pomdpProblem.observationProbabilities[s_][a][o];
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

    private void updateUb(Partition partition, double[] belief) throws IloException {
        partition.ubFunction.addPoint(belief, computeHV(belief));
    }

    private double multiply(double[] a, double[] b) {
        assert a.length == b.length;
        double value = 0;
        for (int i = 0; i < a.length; i++) {
            value += a[i] * b[i];
        }
        return value;
    }

    private AlphaVector<Integer> getBestAlphaVector(double[] belief, int action, int observation) {
        double[] nextBelief = partition.nextBelief(belief, action, observation);
        if (nextBelief == null) return null;
        // find best alpha vector
        AlphaVector<Integer> bestAlpha = null;
        double valueOfBestAlpha = -1;

        for (AlphaVector<Integer> alphaVector : partition.lbFunction.getVectors()) {
            // multiplication
            double value = multiply(alphaVector.vector, nextBelief);
            if (value > valueOfBestAlpha) {
                bestAlpha = alphaVector;
                valueOfBestAlpha = value;
            }
        }
        if (bestAlpha == null) throw new RuntimeException();
        return bestAlpha;

    }

    private void updateLb(Partition partition, double[] belief) throws IloException {
        // [paper Alg 3]
        ArrayList<AlphaVector<Integer>> betasAo = new ArrayList<>(pomdpProblem.getNumberOfObservations());
        double[] betaVec;
        double[] maxBetaVec = null;
        double maxBetaVecValue = Double.NEGATIVE_INFINITY;
        double sumOs_, betaVecValue;
        int bestA = 0;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                betasAo.add(partition.getAlphaDotProdArgMax(belief, a, o));
            }
            betaVec = new double[belief.length];
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                sumOs_ = 0;
                for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                    for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                        sumOs_ += HelperFunctions.dotProd(betasAo.get(o), s_, belief[s_]) *
                                pomdpProblem.observationProbabilities[s_][a][o] *
                                pomdpProblem.actionProbabilities[s][a][s_];
                    }
                }
                betaVec[s] = pomdpProblem.rewards[s][a] + pomdpProblem.discount * sumOs_;
            }
            betaVecValue = HelperFunctions.dotProd(betaVec, belief);
            if (betaVecValue > maxBetaVecValue) {
                maxBetaVecValue = betaVecValue;
                maxBetaVec = betaVec;
                bestA = a;
            }
        }
        partition.lbFunction.addVector(maxBetaVec, bestA);
    }

    private double width(Partition partition, double[] belief) {
        return partition.ubFunction.getValue(belief) - partition.lbFunction.getValue(belief);
    }

}
