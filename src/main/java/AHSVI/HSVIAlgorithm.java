package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    public Partition partition;

    public double finalUtilityLB;
    public double finalUtilityUB;
    public double minLB, minUB;

    public HSVIAlgorithm(POMDPProblem pomdpProblem) {
        try {
            Cplex.get().setParam(IloCplex.IntParam.RootAlg, 2);
        } catch (IloException e) {
            e.printStackTrace();
        }
        this.pomdpProblem = pomdpProblem;
        this.partition = new Partition(0, pomdpProblem);
        this.partition.initValueFunctions();
    }

    public void solve(Partition initialPartition, double epsilon) throws IloException {
        double ePrime;
        while (true) {
            double width = width(initialPartition, initialBelief);

            minLB = this.partition.lbFunction.getValue(initialBelief);
            minUB = this.partition.ubFunction.getValue(initialBelief);

            if ((minUB - minLB) / minUB < epsilon / 100) {
                break;
            }

            ePrime = epsilon / 100d;

            explore(initialPartition, initialBelief, ePrime, 0);
        }


        double width = width(initialPartition, initialBelief);
        int numPoints = partition.ubFunction.numPoints();
        int numVectors = partition.lbFunction.numVectors();

        this.finalUtilityLB = partition.lbFunction.getValue(initialBelief);
        this.finalUtilityUB = partition.ubFunction.getValue(initialBelief);

    }

    private void explore(Partition partition, double[] belief, double ePrime, int t) throws IloException {
        Triplet<Integer, Integer, double[]> aoPair = select(partition, belief, ePrime, t + 1);
        if (aoPair != null) {
            explore(partition, aoPair.getThird(), ePrime, t + 1);
        } else {
            System.out.println(" Depth " + t);
        }

        updateLb(partition, belief);
        updateUb(partition, belief);
    }

    private Triplet<Integer, Integer, double[]> select(Partition partition, double[] belief, double ePrime, int t) throws IloException {
        //TODO change this function
        double gamma = pomdpProblem.discount;
        Triplet<Integer, Integer, double[]> best = null;

        double ePrimeValue = ePrime;

        ePrimeValue /= Math.pow(gamma, t);

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
        for (int o = 0; o < observationsCount; ++o) {
            double[] nextBelief = partition.nextBelief(belief, bestA, o);
            if (nextBelief != null) {
                double prb = pomdpProblem.getProbabilityOfObservationPlayingAction(bestA, o);
                double excess = width(partition, nextBelief) - ePrimeValue;
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
        nextState:
        for (int i = 0; i < belief.length; i++) {
            if (belief[i] < Config.ZERO) {
//            if ( belief[i] == 0 ) {
                continue;
            }

            cz.agents.deceptiongame.dynprog.auxiliary.Pair<UserTypeI, Long> userTypeIIntegerPair = game.indexToState.get(i);

//            game.observations.stream().mapToDouble()
//            System.out.println();
//            double sum = 0;
//            for ( int obInt = 0; obInt < game.observations.size()-1; obInt++ ) {
//                int observation = game.observations.get(obInt);
//                double probabilityOfObservation = userTypeIIntegerPair.isInCurb().getProbabilityOfObservationToNextStep(observation);
//                sum += probabilityOfObservation;
//            }

            for (int obInd = 0; obInd < game.thresholds.size(); obInd++) {
//                long observation = game.thresholds.get(obInd);

//                double probabilityOfObservation = userTypeIIntegerPair.isInCurb().getProbabilityOfObservation(observation, game.observationStep);
                double probabilityOfObservation = userTypeIIntegerPair.getLeft().getProbabilityOfObservationToNextStep(obInd);
                assert probabilityOfObservation <= 1d && probabilityOfObservation >= 0;

//                if ( probabilityOfObservation < Config.ZERO ) {
                if (probabilityOfObservation == 0) {
                    continue;
                }


                double[] next = partition.nextBelief(belief, actionIndex, obInd);
                if (next != null) {
                    // TODO: new
                    double prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight()), actionIndex, obInd, game.IS_ADDITIVE);
                    assert prbOfNotDetecting <= 1d + Config.ZERO && prbOfNotDetecting >= -Config.ZERO;
                    if (prbOfNotDetecting == 0) {
                        continue;
                    }
                    if (prbOfNotDetecting > 1 + Config.ZERO || prbOfNotDetecting < 0 - Config.ZERO) {
                        System.err.println("probability is " + prbOfNotDetecting);
                        prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight()), actionIndex, obInd, game.IS_ADDITIVE);
                    }
                    assert prbOfNotDetecting >= -Config.ZERO && prbOfNotDetecting <= 1 + Config.ZERO : "prb is " + prbOfNotDetecting;

                    immediateReward += belief[i] * probabilityOfObservation * prbOfNotDetecting * (game.thresholds.get(actionIndex) + game.discount * partition.ubFunction.getValue(next));
//                immediateReward += next[i] * probabilityOfObservation * prbOfNotDetecting * (game.thresholds.get(actionIndex) + game.discount * partition.ubFunction.getValue(next));

                    // TODO:new
                    // TODO: old
//                    if ( game.IS_ADDITIVE && userTypeIIntegerPair.getAttError() >= action + observation ) {
//                        immediateReward += belief[i] * probabilityOfObservation * (action + game.discount * partition.ubFunction.getValue(next));
//                    } else if ( !game.IS_ADDITIVE && userTypeIIntegerPair.getAttError() >= action ) {
//                        immediateReward += belief[i] * probabilityOfObservation * (action + game.discount * partition.ubFunction.getValue(next));
//                    } else {
//                         once action (+ observation) surpassed the threshold, for any larger observation it will also surpass!
//                         goto next state
//                        continue nextState;
//                    }
                    // TODO:old
                }

                /*
                if ( game.IS_ADDITIVE && userTypeIIntegerPair.getAttError() <= action + observation ) {
                    immediateReward += action * probabilityOfObservation * belief[i] + game.discount * partition.ubFunction.getValue(partition.nextBelief(belief, action, observation));
                } else if ( !game.IS_ADDITIVE && userTypeIIntegerPair.getAttError() <= action ) {
                    immediateReward += action * probabilityOfObservation * belief[i] + game.discount * partition.ubFunction.getValue(partition.nextBelief(belief, action, observation));
                }
                */
            }
        }

        return immediateReward;
    }

    private void updateUb(Partition partition, double[] belief) throws IloException {

        // get best action
        double bestValue = 0;
        int bestAction = 0;
        for (int actionIndex = 0; actionIndex < game.thresholds.size(); actionIndex++) {
//        for (Integer action : game.actions) {
            double v = computeQub(belief, actionIndex);
            if (v > bestValue) {
                bestValue = v;
                bestAction = actionIndex;
            }
        }

        partition.ubFunction.addPoint(belief, bestValue, null);
        System.out.println(partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO);

        assert partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO : partition.lbFunction.getValue(belief) + " < " + partition.ubFunction.getValue(belief) + " bestValue=" + bestValue + " bestAction=" + bestAction;
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
//        for (Integer action : game.actions) {
//            for (Integer observation : game.observations) {
                AlphaVector<Integer> alpha = null;

                nextState:
                for (int state = 0; state < belief.length; state++) {
                    cz.agents.deceptiongame.dynprog.auxiliary.Pair<UserTypeI, Long> userType = game.indexToState.get(state);
                    // todo: old
//                    if ( game.IS_ADDITIVE && action + observation > userType.getAttError() ) {
//                        continue nextState;
//                    } else if ( !game.IS_ADDITIVE && action > userType.getAttError() ) {
//                        continue nextState;
//                    }
//                    double prbOfNotDet = userType.isInCurb().getProbabilityOfObservation(observation);
                    // TODO: old
                    // todo: new
//                    double prbOfNotDet = userType.isInCurb().getProbabilityOfNotDetectingNormalized(userType.getAttError(), action, observation, game.IS_ADDITIVE);
                    double prbOfNotDet = userType.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userType.getRight()), actionInd, obInd, game.IS_ADDITIVE);
                    prbOfNotDet *= userType.getLeft().getProbabilityOfObservationToNextStep(obInd);
                    // todo: new
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
//        for (Integer action : game.actions) {
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
//        assert partition.lbFunction.getValue(belief) <= partition.ubFunction.getValue(belief);

        /*
        MultiKeyMap map = new MultiKeyMap();
//        for ( int state=0; state<belief.length; state++ ) {
            for (Integer action : game.actions) {
                for (Integer observation : game.observations) {
                    double[] nextBelief = partition.nextBelief(belief, action, observation);
                    // find best alpha vector
//                    cz.agents.deceptiongame.thresholdGame.hsvi.AlphaVector<PolicyNode> bestAlpha = null;
                    AlphaVector<PolicyNode> bestAlpha = null;
                    double valueOfBestAlpha = 0;

                    for (AlphaVector<PolicyNode> alphaVector : partition.lbFunction.getVectors()) {
                        // multiplication
                        double value = multiply(alphaVector.vector, nextBelief);
                        if ( value > valueOfBestAlpha ) {
                            bestAlpha = alphaVector;
                            valueOfBestAlpha = value;
                        }
                    }
//                    map.put(state, action, observation, bestAlpha);
                    map.put(action, observation, bestAlpha);
                }
            }
//        }

        MultiKeyMap margined = new MultiKeyMap();
        // for each state x action
        for ( int state = 0; state<belief.length; state++ ) {
            for (Integer action : game.actions) {

                for (Integer observation : game.observations) {
                    for ( int statePrime = 0; statePrime<belief.length; statePrime++ ) {
                        cz.agents.deceptiongame.dynprog.auxiliary.Pair<UserTypeI, Integer> stateImpl = game.indexToState.get(state);
                        double reward = observation + action <= stateImpl.getAttError() ? action : 0;
                        double obserPrb = stateImpl.isInCurb().getProbabilityOfObservation(observation);
                        reward *= obserPrb;

                        ((AlphaVector)map.get(action, observation)).
                        double rest = game.discount * ((AlphaVector)map.get(action, observation)).vector[statePrime] * obserPrb
                    }
                }

            }
        }

        partition.lbFunction.getVectors()



*/


//        Solution solution = partition.solveLB(belief, true);
//        StrategyNode node = new StrategyNode(partition, solution);
//        double[] values = node.getValues();

//        if(Config.DEBUG) {
//            Cplex.get().setName(Arrays.toString(belief));
//            Cplex.get().exportModel("problem.lp");
//
//            double[] altValues = new StrategyNode(partition, solution).getValues();
//            double diff = 0;
//            for(int i = 0 ; i < values.length ; i++) {
//                diff += Math.abs(values[i] - altValues[i]);
//            }
//            assert diff < Config.ZERO;
//        }

//        partition.lbFunction.addVector(values, node);
    }

    private double width(Partition partition, double[] belief) {
        double lowerValue = partition.lbFunction.getValue(belief);
        double upperValue = partition.ubFunction.getValue(belief);
        return upperValue - lowerValue;
    }

}
