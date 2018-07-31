package ahsvi.pomdpproblem;

import java.util.HashMap;

/**
 *
 * @author dansm
 */
public class POMDPBelief {
    private final HashMap<POMDPState, Double> probDist;

    public POMDPBelief() {
        this.probDist = new HashMap<>();
    }
    
    public double getProbInState(POMDPState state) {
        return probDist.getOrDefault(state, 0.0);
    }
    
    public void setProbInState(POMDPState state, double prob) {
        probDist.put(state, prob);
    }
}
