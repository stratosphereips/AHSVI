package ahsvi.pomdpproblem;

import java.util.HashMap;

/**
 *
 * @author dansm
 */
public class POMDPActionRewards {

    private HashMap<POMDPState, HashMap<POMDPState, HashMap<POMDPObservation, Double>>> rewards;

    public POMDPActionRewards() {
        rewards = new HashMap<>();
    }

    public double getReward(POMDPState startState, POMDPState endState, POMDPObservation observation) {
        HashMap<POMDPState, HashMap<POMDPObservation, Double>> rewardsFromStartState
                = rewards.get(startState);
        if (rewardsFromStartState == null) {
            return 0.0;
        }
        HashMap<POMDPObservation, Double> rewardsFromEndState = rewardsFromStartState.get(endState);
        if (rewardsFromEndState == null) {
            return 0.0;
        }
        Double reward = rewardsFromEndState.get(observation);
        if (rewardsFromEndState == null) {
            return 0.0;
        }
        return reward;
    }

    public void setReward(POMDPState startState, POMDPState endState, POMDPObservation observation, double reward) {
        HashMap<POMDPState, HashMap<POMDPObservation, Double>> rewardsFromStartState
                = rewards.get(startState);
        if (rewardsFromStartState == null) {
            rewardsFromStartState = new HashMap<>();
            rewards.put(startState, rewardsFromStartState);
        }
        HashMap<POMDPObservation, Double> rewardsFromEndState = rewardsFromStartState.get(endState);
        if (rewardsFromEndState == null) {
            rewardsFromEndState = new HashMap<>();
            rewardsFromStartState.put(endState, rewardsFromEndState);
        }
        rewardsFromEndState.put(observation, reward);
    }
}
