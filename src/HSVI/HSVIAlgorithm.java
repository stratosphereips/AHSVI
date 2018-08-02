package HSVI;

import POMDPProblem.POMDPProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.Iterator;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    public Partition partition;

    public double finalUtilityLB;
    public double finalUtilityUB;
    public int numberOfCalls = 1;
    public double minLB, minUB;

    public HSVIAlgorithm(POMDPProblem pomdpProblem) {
        try {
            Cplex.get().setParam(IloCplex.IntParam.RootAlg, 2);
        } catch (IloException e) {
            e.printStackTrace();
        }
//        if ( MINIMIZE_VALUE ) {
//        } else {
//            game.prepareStateIndexingOnlySupport();
//        }
        this.pomdpProblem = pomdpProblem;
        this.partition = new Partition(0, pomdpProblem);
        this.partition.initValueFunctions();
    }

    public void setStopwatch(Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
    }

    public void solve(Partition initialPartition, double epsilon) throws IloException {
        int removed = 0;
        long next = 0;

        if (HSVIAlgorithm.MINIMIZE_VALUE) {
            this.partition.lbFunction.updateMinimumWithFP(game);

            initialBelief = this.partition.lbFunction.minimalFPBelief;
            game.updatePrior(initialBelief);
            String s = game.stgForMathematicaCumulativeString();
            System.out.println("Def strategy :\n" + s);
//                System.out.println("FP: " + game.prior.getFalsePositives());
            System.out.println("UpperBound minimum is : " + this.partition.ubFunction.getMinimum());
            this.partition.ubFunction.getValueFast(initialBelief);
        }

        double prevMinLB = -1;
        while(true) {
            double width = width(initialPartition, initialBelief);
            if(stopwatch.elapsed(TimeUnit.MILLISECONDS) >= next) {
                int numPoints = 0;
                int numVectors = 0;
                numPoints = partition.ubFunction.numPoints();
                numVectors = partition.lbFunction.numVectors();
                System.out.printf("%8d %.5f %.5f %.5f %7d %7d\n", stopwatch.elapsed(TimeUnit.MILLISECONDS), initialPartition.lbFunction.getValue(initialBelief), initialPartition.ubFunction.getValue(initialBelief), width, numVectors, numPoints);
            }

            if ( MINIMIZE_VALUE ) {
                this.partition.lbFunction.updateMinimumWithFP(game);
                this.partition.ubFunction.updateMinimumWithFP(game);
                minLB = this.partition.lbFunction.minimumFP;
                minUB = this.partition.ubFunction.minimumFP;
                System.out.println("minLB=" + minLB + ", " + this.partition.ubFunction.getValue(this.partition.lbFunction.minimalFPBelief));
//                System.out.println("minUB=" + this.partition.lbFunction.getValue(this.partition.ubFunction.minimalFPBelief) + ", " + minUB);
                System.out.println("minUB=" + this.partition.lbFunction.getValue(this.partition.ubFunction.minimalFPBelief) + ", " + minUB);
            } else {
                minLB = this.partition.lbFunction.getValue(initialBelief);
                minUB = this.partition.ubFunction.getValue(initialBelief);
                System.out.println("minLB=" + minLB);
                System.out.println("minUB=" + minUB);
            }

//            System.out.println("new " + this.partition.ubFunction.minimumFP/this.partition.lbFunction.minimumFP );

            double e = (minUB - minLB)/minUB;

            prevMinLB = minLB;

            System.out.println((minUB - minLB)/minUB + " vs " + epsilon/100);
            if ((minUB - minLB)/minUB < epsilon/100) {
                System.out.println("Breaking!!");
                System.out.println((minUB - minLB)/minUB + " vs " + epsilon/100);
                break;
            }
//            System.out.println("Width=" + width + " vs " + this.partition.lbFunction.minimumFP * epsilon / 100d);
//            if (width <= (double) this.partition.lbFunction.minimumFP * epsilon / 100d) {
//                System.out.println("Break2, width<=epsilon " + width + "<=" + (double) this.partition.lbFunction.minimumFP * epsilon / 100d);
//                break;
//            }

            if(System.getProperty("hsvi.terminate") != null) {
                if(width <= Double.parseDouble(System.getProperty("hsvi.terminate"))) break;
            }

            if(System.getProperty("hsvi.timeLimit") != null) {
                if(stopwatch.elapsed(TimeUnit.MILLISECONDS) >= Long.parseLong(System.getProperty("hsvi.timeLimit"))) return;;
            }

//            double ePrime = epsilon/2 + 0.9*(width);
            double ePrime = epsilon/100d;
            System.out.println("ePrime=" + ePrime);
//            double ePrime = epsilon / 2d;
            Q = 0; // TODO: is it so?
//            updateQ(epsilon, ePrime);

            stopwatch.start();
            stopwatch.run("EXPLORE", Stopwatch.THREAD);
            System.out.println("num of calls: " + numberOfCalls);
//            checkIfLBisBelowUB();
            explore(initialPartition, initialBelief, ePrime, 0);
            numberOfCalls++;
            stopwatch.done();
            stopwatch.stop();


            System.out.println("New value at old init belief is " + this.partition.lbFunction.getValue(initialBelief));
            if ( HSVIAlgorithm.MINIMIZE_VALUE ) {
                this.partition.lbFunction.updateMinimumWithFP(game);
                this.partition.ubFunction.updateMinimumWithFP(game);

                initialBelief = this.partition.lbFunction.minimalFPBelief;
                game.updatePrior(initialBelief);
                String s = game.stgForMathematicaCumulativeString();
                System.out.println("Def strategy :\n" + s);
//                System.out.println("FP: " + game.prior.getFalsePositives());
//                System.out.println("UpperBound minimum is : " + this.partition.ubFunction.getMinimum());
//                this.partition.ubFunction.getValueFast(initialBelief);

            }

//            System.out.println("New Initial Belief is (" + this.partition.lbFunction.minimumFP + ") : ");
//            game.strategyToString(initialBelief);

            Timing.print();
            Timing.clear();
        }


        double width = width(initialPartition, initialBelief);
        int numPoints = partition.ubFunction.numPoints();
        int numVectors = partition.lbFunction.numVectors();
        System.out.printf("%8d %.5f %.5f %.5f %7d %7d\n", stopwatch.elapsed(TimeUnit.MILLISECONDS), initialPartition.lbFunction.getValue(initialBelief), initialPartition.ubFunction.getValue(initialBelief), width, numVectors, numPoints);
//        this.minLBBelief = initialBelief;


        if ( MINIMIZE_VALUE ) {
            this.partition.lbFunction.updateMinimumWithFP(game);
            this.partition.ubFunction.updateMinimumWithFP(game);
            this.finalUtilityLB = partition.lbFunction.getValue(this.partition.lbFunction.minimalFPBelief);
            this.finalUtilityUB = partition.ubFunction.getValue(this.partition.ubFunction.minimalFPBelief);
        } else {
            this.finalUtilityLB = partition.lbFunction.getValue(initialBelief);
            this.finalUtilityUB = partition.ubFunction.getValue(initialBelief);
        }

        this.timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);

    }


//    private void updateQ(double epsilon, double ePrime) {
//        double maxGap = Double.NEGATIVE_INFINITY;
//        for(Partition p : game.getPartitions()) {
//            maxGap = Math.max(maxGap, p.ubFunction.getMaximum() - p.lbFunction.getMinimum());
//        }
//
//        int tMax = (int)Math.ceil((Math.log(maxGap) - Math.log(epsilon)) / Math.log(1.0 / game.getGamma()));
//        double G = Math.pow(game.getGamma(), -tMax);
//        Q = (ePrime - epsilon) * G * (game.getGamma() - 1) / (1 - G);
//    }

    private void explore(Partition partition, double[] belief, double ePrime, int t) throws IloException {

        if ( !MINIMIZE_VALUE ) {
            if ( t < numberOfCalls ) {
                Triplet<Integer, Integer, double[]> aoPair = select(partition, belief, ePrime, t + 1);
                if (aoPair != null) {
                    System.out.println("Action=" + aoPair.getFirst() + ", Observation=" + aoPair.getSecond());
                    explore(partition, aoPair.getThird(), ePrime, t + 1);
                } else {
                    System.out.println(" Depth " + t);
                }
            }
        } else {
//            if ( t < Math.sqrt(numberOfCalls) ) {
            if ( t < numberOfCalls ) {
                Triplet<Integer, Integer, double[]> aoPair = select(partition, belief, ePrime, t + 1);
                if (aoPair != null) {
                    System.out.println("Action=" + aoPair.getFirst() + ", Observation=" + aoPair.getSecond());
                    explore(partition, aoPair.getThird(), ePrime, t + 1);
                } else {
                    System.out.println(" Depth " + t);
                }
            } else {
                System.out.println(" Depth " + t);
            }
        }

//        if(ubLookahead) {
//            double[][] followerStrategy = partition.solveUB(belief).getFollowerStrategy();
//            for(Action a : partition.leaderActions) {
//                for(Observation o : partition.getObservations(a)) {
//                    double[] transformed = partition.getTransformedBelief(belief, a, o, followerStrategy);
//                    if(transformed == null) continue;
//                    updateUb(partition.getTransition(a, o), transformed);
//                }
//            }
//        }


//        checkIfLBisBelowUB();

        assert partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO : partition.lbFunction.getValue(belief) +" < " + partition.ubFunction.getValue(belief);
        assert width(partition, this.initialBeliefe) >= 0;
        updateLb(partition, belief);

//        checkIfLBisBelowUB();

        assert partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO : partition.lbFunction.getValue(belief) +" < " + partition.ubFunction.getValue(belief);
        System.out.println(width(partition, this.initialBeliefe) >= 0);
        assert width(partition, this.initialBeliefe) >= 0;
        updateUb(partition, belief);
        assert partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO : partition.lbFunction.getValue(belief) +" < " + partition.ubFunction.getValue(belief);
        System.out.println(width(partition, this.initialBeliefe) >= 0);
        assert width(partition, this.initialBeliefe) >= 0;

//        checkIfLBisBelowUB();
    }

    private void checkIfLBisBelowUB() {
        Iterator iterator = partition.ubFunction.iterator();
        int counter = 0;
        while ( iterator.hasNext() ) {
            PointBasedValueFunction.Point p = (PointBasedValueFunction.Point)iterator.next();
            System.out.println(counter + ": " + partition.lbFunction.getValue(p.coordinates) + " < " + partition.ubFunction.getValue(p.coordinates));
            counter++;
            assert partition.lbFunction.getValue(p.coordinates) < partition.ubFunction.getValue(p.coordinates) + Config.ZERO : partition.lbFunction.getValue(p.coordinates) +" < " + partition.ubFunction.getValue(p.coordinates);
        }
    }

    private Triplet<Integer,Integer,double[]> select(Partition partition, double[] belief, double ePrime, int t) throws IloException {

        double gamma = game.discount;
        Triplet<Integer, Integer, double[]> best = null;

        int i = 0;

        double ePrimeValue = ePrime;
        double circleValue = 0.0;

        ePrimeValue /= Math.pow(gamma, t);

        // added by me
//        System.out.println("selecting action");
        int bestActionIndex = 0;
        double valueOfBestAction = -1;
        for ( int actionIndex = 0; actionIndex < game.thresholds.size(); actionIndex++ ) {
//        for (Integer action : game.actions) {

            // compute lower QV
            double value = computeQub(belief, actionIndex);

//            System.out.println(value);
            if ( value > valueOfBestAction ) {
                bestActionIndex = actionIndex;
                valueOfBestAction = value;
            }
//            else {
//                break;
//            }
        }

//        System.out.println("selecting observation");
        // compute best observation
        Integer bestObservation = 0;
        double valueOfBestObservation = 0;
        for (int obIndex = 0; obIndex < game.thresholds.size(); obIndex++ ) {
//        for (Integer observation : game.observations) {
            double[] nextBelief = partition.nextBelief(belief, bestActionIndex, obIndex);
            if ( nextBelief == null ) continue;
            double prb = partition.getObservationProbability(belief, bestActionIndex, obIndex);
            double excess = width(partition, nextBelief) - ePrimeValue;
            double value = prb * excess;
            if ( value > valueOfBestObservation ) {
                bestObservation = obIndex;
                valueOfBestObservation = value;
            }
        }
        // added by me

        double[] nextBel = partition.nextBelief(belief, bestActionIndex, bestObservation);
        if(valueOfBestObservation > 0) best = new Triplet<>(bestActionIndex, bestObservation, nextBel);

        return best;

    }

    private double computeQub(double[] belief, int actionIndex) {
        double immediateReward = 0;
        nextState: for ( int i=0; i<belief.length; i++ ) {
            if ( belief[i] < Config.ZERO ) {
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

            for ( int obInd = 0; obInd < game.thresholds.size(); obInd++ ) {
//                long observation = game.thresholds.get(obInd);

//                double probabilityOfObservation = userTypeIIntegerPair.isInCurb().getProbabilityOfObservation(observation, game.observationStep);
                double probabilityOfObservation = userTypeIIntegerPair.getLeft().getProbabilityOfObservationToNextStep(obInd);
                assert probabilityOfObservation <= 1d && probabilityOfObservation >= 0;

//                if ( probabilityOfObservation < Config.ZERO ) {
                if ( probabilityOfObservation == 0 ) {
                    continue;
                }


                double[] next = partition.nextBelief(belief, actionIndex, obInd);
                if(next != null) {
                    // TODO: new
                    double prbOfNotDetecting = userTypeIIntegerPair.getLeft().getProbabilityOfNotDetectingNormalized(game.getDefendersThresholdActionInverse(userTypeIIntegerPair.getRight()), actionIndex, obInd, game.IS_ADDITIVE);
                    assert prbOfNotDetecting <= 1d + Config.ZERO && prbOfNotDetecting >= -Config.ZERO;
                    if ( prbOfNotDetecting == 0 ) {
                        continue;
                    }
                    if ( prbOfNotDetecting > 1 + Config.ZERO || prbOfNotDetecting < 0 - Config.ZERO ) {
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

        // future reward
        /*
        double futureReward = 0;
        for (Integer observation : game.observations) {
            double[] newBelief = partition.nextBelief(belief, action, observation);
            if ( newBelief == null ) continue;
            double prb = partition.getObservationProbability(belief, action, observation);
            double value = partition.ubFunction.getValue(newBelief);
            futureReward += prb * value;
        }
        return immediateReward + game.discount * futureReward;
        */

        return immediateReward;
    }

    private void updateUb(Partition partition, double[] belief) throws IloException {

        // get best action
        double bestValue = 0;
        int bestAction = 0;
        for (int actionIndex = 0; actionIndex < game.thresholds.size(); actionIndex++ ) {
//        for (Integer action : game.actions) {
            double v = computeQub(belief, actionIndex);
            if ( v > bestValue ) {
                bestValue = v;
                bestAction = actionIndex;
            }
        }

        partition.ubFunction.addPoint(belief, bestValue, null);
        System.out.println(partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO);

        assert partition.lbFunction.getValue(belief) < partition.ubFunction.getValue(belief) + Config.ZERO : partition.lbFunction.getValue(belief) +" < " + partition.ubFunction.getValue(belief) + " bestValue=" + bestValue + " bestAction=" + bestAction;
    }

    private double multiply(double[] a, double[] b){
        assert a.length == b.length;
        double value = 0;
        for (int i=0; i<a.length; i++ ) {
            value += a[i] * b[i];
        }
        return value;
    }

    private AlphaVector<Integer> getBestAlphaVector(double[] belief, int action, int observation) {
        double[] nextBelief = partition.nextBelief(belief, action, observation);
        if(nextBelief == null) return null;
        // find best alpha vector
        AlphaVector<Integer> bestAlpha = null;
        double valueOfBestAlpha = -1;

        for (AlphaVector<Integer> alphaVector : partition.lbFunction.getVectors()) {
            // multiplication
            double value = multiply(alphaVector.vector, nextBelief);
            if ( value > valueOfBestAlpha ) {
                bestAlpha = alphaVector;
                valueOfBestAlpha = value;
            }
        }
        if(bestAlpha==null) throw new RuntimeException();
        return bestAlpha;

    }

    private void updateLb(Partition partition, double[] belief) throws IloException {
        MultiKeyMap map = new MultiKeyMap();

        for ( int actionInd = 0; actionInd < game.thresholds.size(); actionInd++ ) {
            for ( int obInd = 0; obInd < game.thresholds.size(); obInd++ ) {
//        for (Integer action : game.actions) {
//            for (Integer observation : game.observations) {
                AlphaVector<Integer> alpha = null;

                nextState: for ( int state = 0; state < belief.length; state++ ) {
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
                    if(prbOfNotDet < Config.ZERO || Double.isNaN(prbOfNotDet)) continue;

                    if ( alpha == null ) {
                        alpha = getBestAlphaVector(belief, actionInd, obInd);
                        if (alpha == null ) continue;
                    }
                    double value = prbOfNotDet * (game.getAttackerUtilityForAction(actionInd) + game.discount * alpha.vector[state]);
                    if ( map.containsKey(actionInd, state) ) {
                        value += (double)map.get(actionInd, state);
                    }
                    map.put(actionInd, state, value);
                }
            }
        }

        // pick best beta_a
        double[] bestVector = null;
        double bestValue = -1;
        Integer bestAction = null;
        for ( int actionInd = 0; actionInd < game.thresholds.size(); actionInd++ ) {
//        for (Integer action : game.actions) {
            double[] currentVector = new double[belief.length];
            double currentValue = 0;

            for  ( int state = 0; state < belief.length; state++ ) {
                if ( map.containsKey(actionInd, state) ) {
                    currentVector[state] = (double)map.get(actionInd, state);
                } else {
                    currentVector[state] = 0;
                }
                currentValue += belief[state] * currentVector[state];
            }

            if ( currentValue > bestValue ) {
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
