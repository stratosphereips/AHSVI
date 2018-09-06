package networkproblem;

import java.util.Collections;

public class State {

    private final Network network;
    private final int numberOfAttackOnHoneypot;
    private final boolean finalState;
    private final String name;

    public State(Network network, int numberOfAttackOnHoneypot) {
        this.network = network;
        Collections.sort(this.network.getComputers());
        this.numberOfAttackOnHoneypot = numberOfAttackOnHoneypot;
        finalState = false;
        name = createName();
    }

    public State(Network network) {
        this(network, 0);
    }

    public State() {
        network = null;
        numberOfAttackOnHoneypot = -1;
        finalState = true;
        name = "F";
    }

    private String createName() {
        return new StringBuilder("S(").append(network.getStringRepresentation()).
                append(",#ha=").append(numberOfAttackOnHoneypot).append(")").toString();
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

    public boolean isFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        return "State{" +
                "network=" + network +
                ", numberOfAttackOnHoneypot=" + numberOfAttackOnHoneypot +
                '}';
    }
}
