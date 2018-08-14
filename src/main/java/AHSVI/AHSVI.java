package AHSVI;

import HSVI.HSVIAlgorithm;
import POMDPProblem.POMDPProblem;

public class AHSVI {

    private final POMDPProblem pomdpProblem;
    private final double epsilon;
    private final HSVIAlgorithm hsviAlgorithm;

    public AHSVI(POMDPProblem pomdpProblem, double epsilon) {
        this.pomdpProblem = pomdpProblem;
        this.epsilon = epsilon;
        hsviAlgorithm = new HSVIAlgorithm(pomdpProblem, epsilon);
    }

    public void solve() {

    }
}
