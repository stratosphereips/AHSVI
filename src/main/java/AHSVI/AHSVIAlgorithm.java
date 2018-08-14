package AHSVI;

import HSVI.HSVIAlgorithm;
import POMDPProblem.POMDPProblem;

import java.util.Arrays;

public class AHSVIAlgorithm {

    private final POMDPProblem pomdpProblem;
    private final double epsilon;
    private final HSVIAlgorithm hsviAlgorithm;

    public AHSVIAlgorithm(POMDPProblem pomdpProblem, double epsilon) {
        this.pomdpProblem = pomdpProblem;
        this.epsilon = epsilon;
        hsviAlgorithm = new HSVIAlgorithm(pomdpProblem, epsilon);
    }

    public void solve() {
        hsviAlgorithm.solve();
        double[] beliefMinLb = hsviAlgorithm.getLbFunction().getBeliefInMinimum();
        double valueInBeliefMinLb = hsviAlgorithm.getLbFunction().getValue(beliefMinLb);
        double[] beliefMinUb = hsviAlgorithm.getUbFunction().getBeliefInMinimum();
        double valueInBeliefMinUb = hsviAlgorithm.getUbFunction().getValue(beliefMinUb);

        System.out.println("Belief in LB min: " + Arrays.toString(beliefMinLb) + " => " + valueInBeliefMinLb);
        System.out.println("Belief in UB min: " + Arrays.toString(beliefMinUb) + " => " + valueInBeliefMinUb);

    }
}
