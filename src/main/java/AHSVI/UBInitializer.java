package main.java.AHSVI;

import main.java.POMDPProblem.POMDPProblem;

public class UBInitializer {
    private static double EPS = 0.05;
    private static int MAX_ITER_N = 1000000;

    private POMDPProblem pomdpProblem;
    private PointBasedValueFunction ubF;
    double eps;
    int maxIterN;

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
    }

    public void computeInitialUB() {
        // TODO https://github.com/trey0/zmdp/blob/master/src/pomdpBounds/FullObsUBInitializer.cc
        double[] alpha = valueIteration();
        ubF.addPoint(alpha);
    }

    public PointBasedValueFunction getUB() {
        return ubF;
    }

    private void nextAlphaAction(double[] result, int a) {
        // TODO
        // pomdp.Ttr[a][s][s_] is matrix of probabilities of ending in s_ from s
        // mult return matrix product in the first arg
        
    }

    private double valueIterationOneStep(double[] alpha, double[] nextAlpha, double[] naa) {
        HelperFunctions.fillArray(nextAlpha, 0);
        HelperFunctions.fillArray(naa, 0);

        nextAlphaAction(nextAlpha, 0);
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            nextAlphaAction(naa, a);
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                nextAlpha[s] = Math.max(nextAlpha[s], naa[s]);
            }
        }

        HelperFunctions.arrSub(alpha, nextAlpha, naa); // naa serves here instead of tmp array
        double maxResidual = HelperFunctions.infinityNorm(naa);
        HelperFunctions.copyArray(nextAlpha, alpha);
        return maxResidual;
    }

    private double[] valueIteration() {
        double residual = eps + 1;
        double[] alpha = new double[pomdpProblem.getNumberOfStates()];
        double[] nextAlpha = new double[pomdpProblem.getNumberOfStates()];
        double[] naa = new double[pomdpProblem.getNumberOfStates()];
        for (int i = 0; i < maxIterN && residual > eps; ++i) {
            residual = valueIterationOneStep(alpha, nextAlpha, naa);
        }
        return alpha;
    }
}
