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
    public Partition partition; //TODO are there more than one partitions?

    public double finalUtilityLB;
    public double finalUtilityUB;

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

    public void solve() throws IloException {
        int iter = 0;
        double lbVal, ubVal, lastLbVal, lastUbVal;
        System.out.println("###########################################################################");
        System.out.println("###########################################################################");
        ++iter;
        lbVal = partition.lbFunction.getValue(pomdpProblem.initBelief);
        ubVal = partition.ubFunction.getValue(pomdpProblem.initBelief);
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
            lbVal = partition.lbFunction.getValue(pomdpProblem.initBelief);
            ubVal = partition.ubFunction.getValue(pomdpProblem.initBelief);
            System.out.println("Solve iteration: " + iter);
            System.out.println("LB in init belief: " + lbVal);
            System.out.println("Diff to last iteration: " + (lbVal - lastLbVal));
            System.out.println("UB in init belief: " + ubVal);
            System.out.println("Diff to last iteration: " + (ubVal - lastUbVal));
            lastLbVal = lbVal;
            lastUbVal = ubVal;
        }

        this.finalUtilityLB = partition.lbFunction.getValue(pomdpProblem.initBelief);
        this.finalUtilityUB = partition.ubFunction.getValue(pomdpProblem.initBelief);

    }

    private boolean widthLargerThanEps(double[] belief) {
        return width(belief) > epsilon;
    }

    private void explore(double[] belief, int t) throws IloException {
        if (width(belief) <= epsilon * Math.pow(pomdpProblem.discount, -t)) {
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
            nextBelief = partition.nextBelief(belief, bestA, o);
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
                    nextBel = partition.nextBelief(belief, a, o);
                    if (nextBel != null) {
                        observationsValuesSubSum += pomdpProblem.actionProbabilities[s][a][s_] * pomdpProblem.observationProbabilities[s_][a][o] *
                                partition.ubFunction.getValue(nextBel);
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
        partition.ubFunction.addPoint(belief, computeHV(belief));
    }


	private void updateLb_old(double[] belief) {
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
				betasAo.add(partition.getAlphaDotProdArgMax(belief, a, o));
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
			if (partition.lbFunction.contains(betaVec)) {
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
			partition.lbFunction.addVector(maxBetaVec, bestA);
		}
	}
    private void updateLb(double[] belief) {
    	// first part
	    Map<Integer, Map<Integer, AlphaVector<Integer>>> betaAO = new HashMap<>();
	    for (int a = 0; a < pomdpProblem.getNumberOfActions(); a++) {
	    	betaAO.put(a, new HashMap<>());
		    for (int o = 0; o < pomdpProblem.getNumberOfObservations(); o++) {
			    double[] nextBelief = partition.nextBelief(belief, a, o);
			    if (nextBelief == null) {
				    betaAO.get(a).put(o, null);
				    continue;
			    };
			    AlphaVector<Integer> maxVector = null;
			    double maxDotProd = Double.NEGATIVE_INFINITY;
			    double dotProd;
			    for (AlphaVector<Integer> alphaVector : partition.lbFunction.alphaVectors) {
				    dotProd = HelperFunctions.dotProd(alphaVector, nextBelief);
				    if ( dotProd > maxDotProd ) {
				    	maxDotProd = dotProd;
				    	maxVector = alphaVector;
				    }
			    }
			    betaAO.get(a).put(o, maxVector);
		    }
	    }

	     // second part
	    Map<Integer, Double[]> betaA = new HashMap<>();
	    for (int a = 0; a < pomdpProblem.getNumberOfActions(); a++) {
		    Double[] betaVec = new Double[pomdpProblem.getNumberOfStates()];
		    for (int s = 0; s < pomdpProblem.getNumberOfStates(); s++) {
		    	double sumOs_ = 0;
			    for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); s_++) {
				    for (int o = 0; o < pomdpProblem.getNumberOfObservations(); o++) {
				    	if ( betaAO.get(a).get(o) != null ) {
						    sumOs_ += betaAO.get(a).get(o).vector[s_] * pomdpProblem.observationProbabilities[s_][a][o] * pomdpProblem.actionProbabilities[s][a][s_];
					    }
				    }
			    }
			    betaVec[s] = pomdpProblem.rewards[s][a] + pomdpProblem.discount * sumOs_;
		    }
		    betaA.put(a, betaVec);
	    }


	    // part 3
	    double maxVal = Double.NEGATIVE_INFINITY;
	    Double[] maxVec = null;
	    int bestA = 0;
	    for (Integer integer : betaA.keySet()) {
		    double value = HelperFunctions.dotProd(betaA.get(integer), belief);
		    if ( value > maxVal ) {
		    	maxVal = value;
		    	maxVec = betaA.get(integer);
		    	bestA = integer;
		    }
	    }

	    partition.lbFunction.addVector(Stream.of(maxVec).mapToDouble(Double::doubleValue).toArray(), bestA);
    }

    private void updateLb2(double[] belief) {
        // [paper Alg 3]
        ArrayList<ArrayList<AlphaVector<Integer>>> betasAo = new ArrayList<>(pomdpProblem.getNumberOfActions());
        double[] betaVec;
        double[] maxBetaVec = null;
        double maxBetaVecValue = Double.NEGATIVE_INFINITY;
        double sumOs_, betaVecValue;
        int bestA = 0;
        AlphaVector<Integer> beta;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            betasAo.add(new ArrayList<>(pomdpProblem.getNumberOfObservations()));
            for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                betasAo.get(a).add(partition.getAlphaDotProdArgMax(belief, a, o));
            }
        }
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            betaVec = new double[belief.length];
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                sumOs_ = 0;
                for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                    beta = betasAo.get(a).get(o);
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
            betaVecValue = HelperFunctions.dotProd(betaVec, belief);
            if (betaVecValue > maxBetaVecValue) {
                maxBetaVecValue = betaVecValue;
                maxBetaVec = betaVec;
                bestA = a;
            }
        }
        partition.lbFunction.addVector(maxBetaVec, bestA);
    }

    private double width(double[] belief) {
        return partition.ubFunction.getValue(belief) - partition.lbFunction.getValue(belief);
    }

}
