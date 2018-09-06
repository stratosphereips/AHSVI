package hsvi;

import java.util.*;
import java.util.logging.*;

import hsvi.CustomLogger.CustomLogger;
import helpers.HelperFunctions;
import hsvi.bounds.*;
import hsvi.hsvicontrollers.HSVIController;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.SolveMethods;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import pomdpproblem.POMDPProblem;

/**
 * Created by wigos on 3.8.16.
 */
public class HSVIAlgorithm {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private final POMDPProblem pomdpProblem;
    private final double epsilon;
    private final HSVIController hsviController;

    private LowerBound lbFunction;
    private UpperBound ubFunction;

    public HSVIAlgorithm(POMDPProblem pomdpProblem, double epsilon, HSVIController hsviController) {
        this.pomdpProblem = pomdpProblem;
        this.epsilon = epsilon;
        this.hsviController = hsviController;
        this.hsviController.init(this);
    }

    public POMDPProblem getPomdpProblem() {
        return pomdpProblem;
    }

    public double getEpsilon() {
        return epsilon;
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
        return getLBValueInBelief(pomdpProblem.getInitBelief());
    }

    public double getUBValueInBelief(double[] belief) {
        return ubFunction.getValue(belief);
    }

    public double getUBValueInInitBelief() {
        return getUBValueInBelief(pomdpProblem.getInitBelief());
    }

    public void initValueFunctions() {
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
            if (pomdpProblem.getObservationProbabilities(s_, a, o) < Config.ZERO) {
                continue;
            }
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                beliefNew[s_] += pomdpProblem.getTransitionProbability(s, a, s_) * belief[s];
            }
            beliefNew[s_] *= pomdpProblem.getObservationProbabilities(s_, a, o);
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

    public double width(double[] belief) {
        return ubFunction.getValue(belief) - lbFunction.getValue(belief);
    }

    public void solve() {
        hsviController.callPreSolveMethod();
        while (!hsviController.shouldSolveTerminate(pomdpProblem.getInitBelief())) {
            explore(pomdpProblem.getInitBelief(), 0);
            hsviController.callInSolveMethod();
        }
        hsviController.callPostSolveMethod();
    }

    private void explore(double[] belief, int t) {
        if (hsviController.shouldExploreTerminate(belief, t)) {// TODO float instability
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
        double valueOfBestO = Double.NEGATIVE_INFINITY;
        double prb, excess;
        for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
            nextBelief = nextBelief(belief, bestA, o);
            if (nextBelief != null) {
                prb = pomdpProblem.getProbabilityOfObservationPlayingAction(o, belief, bestA);
                excess = width(nextBelief) - epsilon * Math.pow(pomdpProblem.getDiscount(), -(t + 1));
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
            rewardsSum += pomdpProblem.getRewards(s, a) * belief[s];
            observationsValuesSubSum = 0;
            for (int s_ = 0; s_ < pomdpProblem.getNumberOfStates(); ++s_) {
                if (pomdpProblem.getTransitionProbability(s, a, s_) < Config.ZERO) {
                    continue;
                }
                for (int o = 0; o < pomdpProblem.getNumberOfObservations(); ++o) {
                    nextBel = nextBelief(belief, a, o);
                    if (nextBel != null &&
                            pomdpProblem.getObservationProbabilities(s_, a, o) > Config.ZERO) {
                        observationsValuesSubSum += pomdpProblem.getTransitionProbability(s, a, s_) * pomdpProblem.getObservationProbabilities(s_, a, o) *
                                ubFunction.getValue(nextBel);

                    }
                }
            }
            observationsValuesSum += belief[s] * observationsValuesSubSum;
        }
        observationsValuesSum *= pomdpProblem.getDiscount();
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
                                pomdpProblem.getObservationProbabilities(s_, a, o) *
                                pomdpProblem.getTransitionProbability(s, a, s_);
                    }
                }
                betaVec[s] = pomdpProblem.getRewards(s, a) + pomdpProblem.getDiscount() * sumOs_;
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

    static class HSVIAlgorithmBuilder {
        private double epsilon;
        private POMDPProblem pomdpProblem;
        private PreSolveMethod preSolveMethod;
        private InSolveMethod inSolveMethod;
        private PostSolveMethod postSolveMethod;
        private SolveTerminator solveTerminator;
        private ExploreTerminator exploreTerminator;

        HSVIAlgorithmBuilder() {
        }

        HSVIAlgorithmBuilder setEpsilon(double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        HSVIAlgorithmBuilder setPomdpProblem(POMDPProblem pomdpProblem) {
            this.pomdpProblem = pomdpProblem;
            return this;
        }

        HSVIAlgorithmBuilder setPreSolveMethod(PreSolveMethod preSolveMethod) {
            this.preSolveMethod = preSolveMethod;
            return this;
        }

        HSVIAlgorithmBuilder setInSolveMethod(InSolveMethod inSolveMethod) {
            this.inSolveMethod = inSolveMethod;
            return this;
        }

        HSVIAlgorithmBuilder setPostSolveMethod(PostSolveMethod postSolveMethod) {
            this.postSolveMethod = postSolveMethod;
            return this;
        }

        HSVIAlgorithmBuilder setSolveTerminator(SolveTerminator solveTerminator) {
            this.solveTerminator = solveTerminator;
            return this;
        }

        HSVIAlgorithmBuilder setExploreTerminator(ExploreTerminator exploreTerminator) {
            this.exploreTerminator = exploreTerminator;
            return this;
        }

        HSVIAlgorithm build() {
            return new HSVIAlgorithm(pomdpProblem,
                    epsilon,
                    new HSVIController(new SolveMethods(preSolveMethod, inSolveMethod, postSolveMethod),
                            solveTerminator, exploreTerminator));
        }
    }
}
