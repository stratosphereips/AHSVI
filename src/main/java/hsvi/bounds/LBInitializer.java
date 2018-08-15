package hsvi.bounds;

import hsvi.HelperFunctions;
import pomdpproblem.POMDPProblem;

import java.util.Arrays;

public class LBInitializer {

    private POMDPProblem pomdpProblem;
    LowerBound lbF;

    public LBInitializer(POMDPProblem pomdpProblem) {
        this.pomdpProblem = pomdpProblem;
        lbF = new LowerBound(pomdpProblem.getNumberOfStates());
    }

    private void fixedInit() {
        lbF.addVector(new double[pomdpProblem.getNumberOfStates()], 0);
    }

    public void computeInitialLB() {
        // [paper 3.2]
        double minRsa, maxRa = Double.NEGATIVE_INFINITY;
        int bestA = 0;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            minRsa = Double.POSITIVE_INFINITY;
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                minRsa = Math.min(minRsa, pomdpProblem.rewards[s][a]);
            }
            if (minRsa > maxRa) {
                maxRa = minRsa;
                bestA = a;
            }
        }
        double R_ = maxRa / (1 - pomdpProblem.discount);
        double[] initAlpha = new double[pomdpProblem.getNumberOfStates()];
        HelperFunctions.fillArray(initAlpha, R_);
        System.out.println("Initial LB alpha vector: " + Arrays.toString(initAlpha)); //TODO print

        lbF.addVector(initAlpha, bestA);
    }

    public LowerBound getLB() {
        return lbF;
    }
}
