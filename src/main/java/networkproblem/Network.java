package networkproblem;

import java.util.ArrayList;
import java.util.HashSet;

public class Network {

    private static final String VALUES_DELIM = ",";
    private static final String COMPUTERS_DELIM = "\\|";

    private static int networkId = 0;

    private int groupId;
    private double probability;
    private final ArrayList<Computer> computers;

    public Network(String line) {
        String[] lineSplits = line.split(VALUES_DELIM, 2);
        probability = Double.parseDouble(lineSplits[0]);
        computers = parseNetworkString(lineSplits[1]);
        groupId = networkId;
        ++networkId;
    }

    public Network(Network oldNetwork) {
        groupId = oldNetwork.groupId;
        probability = oldNetwork.probability;
        computers = new ArrayList<>(oldNetwork.computers);
    }

    public int getGroupId() {
        return groupId;
    }

    public ArrayList<Computer> getComputers() {
        return computers;
    }

    public double getProbability() {
        return probability;
    }

    public boolean containsComputerAtIndex(int i) {
        return i < computers.size();
    }

    public boolean computerAtIndexIsReal(int i) {
        return computers.get(i).isReal();
    }

    public Computer getComputerAtIndex(int i) {
        return computers.get(i);
    }

    public boolean portInComputerExists(int computerI, int port) {
        return containsComputerAtIndex(computerI) && getComputerAtIndex(computerI).containsPort(port);
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void addComputer(Computer computer) {
        computers.add(computer);
    }

    public HashSet<Integer> getOpenPortsInNetwork() {
        HashSet<Integer> openPortsInNetwork = new HashSet<>();
        for (Computer computer : computers) {
            if (computer.isReal()) {
                openPortsInNetwork.addAll(computer.getPorts());
            }
        }
        return openPortsInNetwork;
    }

    private ArrayList<Computer> parseNetworkString(String networkString) {
        String[] computersStrings = networkString.split(COMPUTERS_DELIM);
        ArrayList<Computer> computersList = new ArrayList<>(computersStrings.length);
        for (int i = 0; i < computersStrings.length; ++i) {
            computersList.add(new Computer(computersStrings[i]));
        }
        return computersList;
    }

    public String getStringRepresentation(boolean infoSet) {
        StringBuilder sb = new StringBuilder(infoSet ? "" : "id:" + groupId + "{");
        for (int computerI = 0; computerI < computers.size(); ++computerI) {
            sb.append(computers.get(computerI).getStringRepresentation(infoSet)).append(computerI < computers.size() - 1 ? "|" : "");
        }
        return sb.append(infoSet ? "" : "}").toString();
    }

    @Override
    public String toString() {
        return "Network{" +
                "groupId=" + groupId +
                ",computers=" + computers +
                '}';
    }
}
