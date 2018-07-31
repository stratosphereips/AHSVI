package ahsvi.pomdpproblem;

import java.util.HashMap;

/**
 *
 * @author dansm
 */
public abstract class POMDPPreambleWithProbDist extends POMDPPreamble{

    private final HashMap<POMDPState, Double> probDist;
    
    public POMDPPreambleWithProbDist(int id, String name) {
        super(id, name);
        probDist = new HashMap<>();
    }
    
    public double getProbInState(POMDPState state) {
        return probDist.getOrDefault(state, 0.0);
    }
    
    public void setTransitionProbFromState(POMDPState state, double prob) {
        if (prob < 0 || prob > 1) {
            throw new IllegalArgumentException("Set transition function: Probabality must be a value between"
                    + "0 and 1");
        }
        probDist.put(state, prob);
    }

}
