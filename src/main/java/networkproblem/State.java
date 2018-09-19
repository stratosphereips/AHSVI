package networkproblem;

import java.util.ArrayList;
import java.util.Collections;

public class State {

    private final Network network;
    private final int numberOfAttackOnHoneypot;
    private final boolean finalState;
    private final String name;
    private final String infoSetName;
    private final String combinationGroupName;

    public State(Network network, int numberOfAttackOnHoneypot) {
        this.network = network;
        this.numberOfAttackOnHoneypot = numberOfAttackOnHoneypot;
        finalState = false;
        name = createName();
        infoSetName = createInfoSetName();
        combinationGroupName = createCombinationGroupName();
    }

    public State(Network network) {
        this(network, 0);
    }

    public State() {
        network = null;
        numberOfAttackOnHoneypot = -1;
        finalState = true;
        name = "F";
        infoSetName = "F";
        combinationGroupName = "F";
    }

    private String getStringRepresentation(ArrayList<Computer> computers, boolean infoSet) {
        StringBuilder sb = new StringBuilder(infoSet ? "" : "id:" + network.getGroupId() + "{");
        for (int computerI = 0; computerI < computers.size(); ++computerI) {
            sb.append(computers.get(computerI).getStringRepresentation(infoSet)).append(computerI < computers.size() - 1 ? "|" : "");
        }
        return sb.append(infoSet ? "" : "}").toString();
    }

    private String createName() {
        return new StringBuilder("S(").append(getStringRepresentation(network.getComputers(), false)).
                append(",#ha=").append(numberOfAttackOnHoneypot).append(")").toString();
    }

    private String createInfoSetName() {
        ArrayList<Computer> sortedComputers = new ArrayList<>(network.getComputers());
        Collections.sort(sortedComputers);
        return getStringRepresentation(sortedComputers, true);
    }

    private String createCombinationGroupName() {
        ArrayList<Computer> sortedComputers = new ArrayList<>(network.getComputers());
        Collections.sort(sortedComputers);
        return getStringRepresentation(sortedComputers, false);
    }

    public Network getNetwork() {
        return network;
    }

    public int getNumberOfAttackOnHoneypot() {
        return numberOfAttackOnHoneypot;
    }

    public String getName() {
        return name;
    }

    public String getInfoSetName() {
        return infoSetName;
    }

    public String getCombinationGroupName() {
        return combinationGroupName;
    }

    public boolean isFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        return "State{" +
                "groupId=" + network.getGroupId() +
                ",network=" + network +
                ", numberOfAttackOnHoneypot=" + numberOfAttackOnHoneypot +
                '}';
    }
}
