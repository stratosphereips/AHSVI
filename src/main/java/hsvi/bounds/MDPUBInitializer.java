package hsvi.bounds;

import helpers.HelperFunctions;
import hsvi.CustomLogger.CustomLogger;
import pomdpproblem.POMDPProblem;
import pomdpproblem.TransitionFunction;

import java.util.Arrays;
import java.util.logging.Logger;

public class MDPUBInitializer {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private static final double EPS = 0.001;
    private static final int MAX_ITER_N = 10000; // TODO constant?

    private final POMDPProblem pomdpProblem;
    private final double[] ubExtremePointsValues;
    private final double eps;
    private final int maxIterN;

    private final TransitionFunction transitionFunction;
    private final double[][] rewards; // r[a][s]
    private final double[] alpha;

    public MDPUBInitializer(POMDPProblem pomdpProblem) {
        this(pomdpProblem, EPS, MAX_ITER_N);
    }

    public MDPUBInitializer(POMDPProblem pomdpProblem, double eps) {
        this(pomdpProblem, eps, MAX_ITER_N);
    }

    public MDPUBInitializer(POMDPProblem pomdpProblem, double eps, int maxIterN) {
        this.pomdpProblem = pomdpProblem;
        this.eps = eps;
        this.maxIterN = maxIterN;
        ubExtremePointsValues = new double[pomdpProblem.getNumberOfStates()];

        alpha = initAlpha();

        transitionFunction = pomdpProblem.getTransitionFunction();
        rewards = transformRewards();
    }

    public void computeInitialUB() {
        // https://github.com/trey0/zmdp/blob/master/src/pomdpBounds/FullObsUBInitializer.cc
        double[] alpha = valueIteration();
        HelperFunctions.copyArray(alpha, ubExtremePointsValues);
        LOGGER.finest("Initial UB points values: " + Arrays.toString(alpha));
    }

    public double[] getInitialUbExtremePointsValues() {
        return ubExtremePointsValues;
    }

    private double[] initAlpha() {
        double maxR = Double.NEGATIVE_INFINITY;
        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
                maxR = Math.max(maxR, pomdpProblem.getRewards(s, a));
            }
        }
        double[] initAlpha = new double[pomdpProblem.getNumberOfStates()];
        HelperFunctions.fillArray(initAlpha, maxR / (1 - pomdpProblem.getDiscount()));
        return initAlpha;
    }

    private double[][] transformRewards() {
        double[][] rewardsTransformed = new double[pomdpProblem.getNumberOfActions()][pomdpProblem.getNumberOfStates()];
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                rewardsTransformed[a][s] = pomdpProblem.getRewards(s, a);
            }
        }
        return rewardsTransformed;
    }

    private void nextAlphaAction(double[] result, int a) {
        HelperFunctions.matrixProd(alpha, transitionFunction, a, result);
        HelperFunctions.arrScalarProd(result, pomdpProblem.getDiscount());
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
