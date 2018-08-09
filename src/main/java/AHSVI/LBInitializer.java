package AHSVI;

import POMDPProblem.POMDPProblem;

import java.util.Arrays;

public class LBInitializer {

    private POMDPProblem pomdpProblem;
    AlphaVectorValueFunction lbF;

    public LBInitializer(POMDPProblem pomdpProblem) {
        this.pomdpProblem = pomdpProblem;
        lbF = new AlphaVectorValueFunction(pomdpProblem.getNumberOfStates());
    }

    private void fixedInit() {
        lbF.addVector(new double[pomdpProblem.getNumberOfStates()], 0);
    }

    public void computeInitialLB() {
        // [paper 3.2]
        fixedInit();
        /*
        double minRsa, minRa;
        double R_ = Double.NEGATIVE_INFINITY;
        int bestA = 0;
        for (int a = 0; a < pomdpProblem.getNumberOfActions(); ++a) {
            minRsa = Double.POSITIVE_INFINITY;
            for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
                minRsa = Math.min(minRsa, pomdpProblem.rewards[s][a]);
            }

            minRa = minRsa / (1 - pomdpProblem.discount);
            if (minRa > R_) {
                R_ = minRa;
                bestA = a;
            }
        }

        double[] initAlpha = new double[pomdpProblem.getNumberOfStates()];
        HelperFunctions.fillArray(initAlpha, R_);
        System.out.println("Initial LB alpha vector: " + Arrays.toString(initAlpha)); //TODO print

        lbF.addVector(initAlpha, bestA);*/
    }

    public AlphaVectorValueFunction<Integer> getLB() {
        return lbF;
    }
}
