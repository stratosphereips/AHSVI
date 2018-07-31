package ahsvi.pomdpproblem;

import java.util.ArrayList;
import java.util.HashSet;


/**
 *
 * @author dansm
 */
public class POMDPProblem {
    private double discount = -1;
    private final ArrayList<POMDPState> states;
    private final ArrayList<POMDPAction> actions;
    private final ArrayList<POMDPObservation> observations;
    private POMDPBelief initialBelief = null;

    public POMDPProblem() {
        states = new ArrayList<>();
        actions = new ArrayList<>();
        observations = new ArrayList<>();
    }

    public double getDiscount() {
        return discount;
    }

    public ArrayList<POMDPState> getStates() {
        return states;
    }

    public ArrayList<POMDPAction> getActions() {
        return actions;
    }

    public ArrayList<POMDPObservation> getObservations() {
        return observations;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
    
    public void addState(POMDPState state) {
        states.add(state);
    }
    
    public void addAction(POMDPAction action) {
        actions.add(action);
    }
    
    public void addObservation(POMDPObservation observation) {
        observations.add(observation);
    }
}
