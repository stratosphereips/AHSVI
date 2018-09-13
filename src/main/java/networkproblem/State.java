package networkproblem;

import java.util.Collections;

public class State {

    private final Network network;
    private final int numberOfAttackOnHoneypot;
    private final boolean finalState;
    private final String name;
    private final String infoSetName;

    public State(Network network, int numberOfAttackOnHoneypot) {
        this.network = network;
        Collections.sort(this.network.getComputers());
        this.numberOfAttackOnHoneypot = numberOfAttackOnHoneypot;
        finalState = false;
        name = createName();
        infoSetName = createInfoSetName();
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
    }

    private String createName() {
        return new StringBuilder("S(").append(network.getStringRepresentation(false)).
                append(",#ha=").append(numberOfAttackOnHoneypot).append(")").toString();
    }

    private String createInfoSetName() {
        return network.getStringRepresentation(true);
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
