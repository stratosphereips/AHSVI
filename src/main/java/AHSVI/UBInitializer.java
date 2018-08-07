package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;

public class UBInitializer {
    private static final double EPS = 0.05;
    private static final int MAX_ITER_N = 1000000;

    private final POMDPProblem pomdpProblem;
    private final PointBasedValueFunction ubF;
    private final double eps;
    private final int maxIterN;

    private final double[][][] actionProbabilities; // p[a][s][s_]
    private final double[][] rewards; // r[a][s]
    private final double[] alpha;

    public UBInitializer(POMDPProblem pomdpProblem) {
        this(pomdpProblem, EPS, MAX_ITER_N);
    }

    public UBInitializer(POMDPProblem pomdpProblem, double eps) {
        this(pomdpProblem, eps, MAX_ITER_N);
    }

    public UBInitializer(POMDPProblem pomdpProblem, double eps, int maxIterN) {
        this.pomdpProblem = pomdpProblem;
        this.eps = eps;
        this.maxIterN = maxIterN;
        ubF = new PointBasedValueFunction(pomdpProblem.getNumberOfStates());

        alpha = new double[pomdpProblem.getNumberOfStates()];
        actionProbabilities = transformActionProbabilities();
        rewards = transformRewards();
    }

    public void computeInitialUB() {
        // TODO https://github.com/trey0/zmdp/blob/master/src/pomdpBounds/FullObsUBInitializer.cc
        double[] alpha = valueIteration();
        ubF.addPoint(alpha);
    }

    public PointBasedValueFunction getUB() {
        return ubF;
    }

    private double[][][] transformActionProbabilities() {
        double[][][] actionProbabilitiesTransformed =
                new double[pomdpProblem.getNumberOfActions()]
                        [pomdpProblem.getNumberOfActions()]
                        [pomdpProblem.getNumberOfActions()];
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s) {
                    actionProbabilitiesTransformed[a][s][s_] = pomdpProblem.actionProbabilities[s][a][s_];
                }
            }
        }
        return actionProbabilitiesTransformed;
    }

    private double[][] transformRewards() {
        double[][] rewardsTransformed = new double[pomdpProblem.getNumberOfActions()][pomdpProblem.getNumberOfStates()];
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                rewardsTransformed[a][s] = pomdpProblem.rewards[s][a];
            }
        }
        return rewardsTransformed;
    }

    private void nextAlphaAction(double[] result, int a) {
        // TODO
        // pomdp.Ttr[a][s][s_] is matrix of probabilities of ending in s_ from s
        // mult return matrix product in the first arg
        //        mult( result, alpha, pomdp->Ttr[a] );
        //        result *= pomdp->discount;
        //        copy_from_column( R_xa, pomdp->R, a );
        //        result += R_xa;
        HelperFunctions.matrixProd(alpha, actionProbabilities[a], result);
        HelperFunctions.arrScalarProd(result, pomdpProblem.discount);
        HelperFunctions.arrAdd(result, rewards[a]);
    }

    private double valueIterationOneStep(double[] nextAlpha, double[] naa) {
        HelperFunctions.fillArray(nextAlpha, 0);
        HelperFunctions.fillArray(naa, 0);

        nextAlphaAction(nextAlpha, 0);
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            nextAlphaAction(naa, a);
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                nextAlpha[s] = Math.max(nextAlpha[s], naa[s]);
            }
        }

        HelperFunctions.arrSub(alpha, nextAlpha, naa); // naa serves here as tmp array
        HelperFunctions.copyArray(nextAlpha, alpha);
        return HelperFunctions.infinityNorm(naa); // compute max residual using infinity norm (max of abs values)
    }


    private double[] valueIteration() {
        double residual = eps + 1;
        double[] nextAlpha = new double[pomdpProblem.getNumberOfStates()];
        double[] naa = new double[pomdpProblem.getNumberOfStates()];
        for (int i = 0; i < maxIterN && residual > eps; ++i) {
            residual = valueIterationOneStep(nextAlpha, naa);
        }
        return alpha;
    }
}
