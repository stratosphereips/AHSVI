package ahsvi.pomdpproblem;


/**
 *
 * @author dansm
 */
public class POMDPAction extends POMDPPreambleWithProbDist{
    
    private final POMDPActionRewards rewards;
    
    public POMDPAction(int id, String name) {
        super(id, name);
        rewards = new POMDPActionRewards();
    }

    public POMDPAction(int id) {
        this(id, null);
    }
    
    public double getReward(POMDPState startState, POMDPState endState, POMDPObservation observation) {
        return rewards.getReward(startState, endState, observation);
    }
}
