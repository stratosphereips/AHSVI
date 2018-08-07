package ahsvi.pomdpproblem;

import java.util.ArrayList;
import java.util.HashSet;


/**
 *
 * @author dansm
 */
public class POMDPProblem {
    private double discount = -1;
    private ArrayList<String> states = null;
    private ArrayList<String> actions = null;
    private ArrayList<String> observations = null;

    public POMDPProblem() {
        states = new ArrayList<>();
        actions = new ArrayList<>();
        observations = new ArrayList<>();
    }
    
}
