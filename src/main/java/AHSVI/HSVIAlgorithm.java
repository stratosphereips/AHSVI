package main.java.AHSVI;

import java.lang.IllegalArgumentException;
import main.java.POMDPProblem.POMDPProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    private final double epsilon;
    public Partition partition;

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
        this.partition = new Partition(0, pomdpProblem);
        this.partition.initValueFunctions();
    }

    public void solve(Partition initialPartition, double epsilon) throws IloException {
        while (widthLargerThanEps(initialPartition, pomdpProblem.initBelief)) {
            explore(initialPartition, pomdpProblem.initBelief);
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

    private void explore(Partition partition, double[] belief) throws IloException {
        Triplet<Integer, Integer, double[]> aoPair = select(partition, belief);
        if (aoPair != null) {
            explore(partition, aoPair.getThird());
        }

        updateLb(partition, belief);
        updateUb(partition, belief);
    }

    private Triplet<Integer, Integer, double[]> select(Partition partition, double[] belief) throws IloException {
        //TODO change this function
        Triplet<Integer, Integer, double[]> best = null;

        int bestA = 0;
        double valueOfBestA = computeQub(belief, 0);
        double value;
        int actionsCount = pomdpProblem.actionNames.size();
        if (actionsCount > 1) {
            for (int a = 1; a < actionsCount; ++a) {
                // compute lower QV
                value = computeQub(belief, a);
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

    private double computeQub(double[] belief, int actionIndex) {
        double immediateReward = 0;
        for (int i = 0; i < belief.length; i++) {
            if (belief[i] < Config.ZERO) {
                continue;
            }

            for (int obInd = 0; obInd < game.thresholds.size(); obInd++) {
                double probabilityOfObservation = userTypeIIntegerPair.getLeft().getProbabilityOfObservationToNextStep(obInd);
                assert probabilityOfObservation <= 1d && probabilityOfObservation >= 0;

                if ( probabilityOfObservation < Config.ZERO ) {
                    continue;
                }


                double[] next = partition.nextBelief(belief, actionIndex, obInd);
                if (next != null) {
                    double prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight()), actionIndex, obInd, game.IS_ADDITIVE);
                    if (prbOfNotDetecting == 0) {
                        continue;
                    }
                    if (prbOfNotDetecting > 1 + Config.ZERO || prbOfNotDetecting < 0 - Config.ZERO) {
                        prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight()), actionIndex, obInd, game.IS_ADDITIVE);
                    }

                    immediateReward += belief[i] * probabilityOfObservation * prbOfNotDetecting * (game.thresholds.get(actionIndex) + game.discount * partition.ubFunction.getValue(next));
                }
            }
        }

        return immediateReward;
    }

    private void updateUb(Partition partition, double[] belief) throws IloException {
        double bestValue = Double.NEGATIVE_INFINITY;
        double v;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); a++) {
            v = computeQub(belief, a);
            if (v > bestValue) {
                bestValue = v;
            }
        }
        partition.ubFunction.addPoint(belief, bestValue, null);
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
        MultiKeyMap map = new MultiKeyMap();

        for (int actionInd = 0; actionInd < game.thresholds.size(); actionInd++) {
            for (int obInd = 0; obInd < game.thresholds.size(); obInd++) {
                AlphaVector<Integer> alpha = null;

                nextState:
                for (int state = 0; state < belief.length; state++) {
                    cz.agents.deceptiongame.dynprog.auxiliary.Pair<UserTypeI, Long> userType = game.indexToState.get(state);
                    double prbOfNotDet = userType.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userType.getRight()), actionInd, obInd, game.IS_ADDITIVE);
                    prbOfNotDet *= userType.getLeft().getProbabilityOfObservationToNextStep(obInd);
                    if (prbOfNotDet < Config.ZERO || Double.isNaN(prbOfNotDet)) continue;

                    if (alpha == null) {
                        alpha = getBestAlphaVector(belief, actionInd, obInd);
                        if (alpha == null) continue;
                    }
                    double value = prbOfNotDet * (game.getAttackerUtilityForAction(actionInd) + game.discount * alpha.vector[state]);
                    if (map.containsKey(actionInd, state)) {
                        value += (double) map.get(actionInd, state);
                    }
                    map.put(actionInd, state, value);
                }
            }
        }

        // pick best beta_a
        double[] bestVector = null;
        double bestValue = -1;
        Integer bestAction = null;
        for (int actionInd = 0; actionInd < game.thresholds.size(); actionInd++) {
            double[] currentVector = new double[belief.length];
            double currentValue = 0;

            for (int state = 0; state < belief.length; state++) {
                if (map.containsKey(actionInd, state)) {
                    currentVector[state] = (double) map.get(actionInd, state);
                } else {
                    currentVector[state] = 0;
                }
                currentValue += belief[state] * currentVector[state];
            }

            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestVector = currentVector;
                bestAction = actionInd;
            }
        }

        partition.lbFunction.addVector(bestVector, bestAction);
    }

    private double width(Partition partition, double[] belief) {
        return partition.ubFunction.getValue(belief) - partition.lbFunction.getValue(belief);
    }

}
