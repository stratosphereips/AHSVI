package networkproblem;

import java.util.ArrayList;
import java.util.HashSet;

public class Network {

    private static final String VALUES_DELIM = ",";
    private static final String COMPUTERS_DELIM = "\\|";

    private double probability;
    private final ArrayList<Computer> realComputers;
    private final ArrayList<Computer> honeyComputers;

    public Network(String line) {
        String[] lineSplits = line.split(VALUES_DELIM, 2);
        probability = Double.parseDouble(lineSplits[0]);
        realComputers = parseNetworkString(lineSplits[1]);
        honeyComputers = null;
    }

    public Network(Network oldNetwork) {
        probability = oldNetwork.probability;
        realComputers = new ArrayList<>(oldNetwork.realComputers);
        honeyComputers = (oldNetwork.honeyComputers == null ? new ArrayList<>() : new ArrayList<>(oldNetwork.honeyComputers));
    }

    public ArrayList<Computer> getRealComputers() {
        return realComputers;
    }

    public ArrayList<Computer> getHoneyComputers() {
        return honeyComputers;
    }

    public double getProbability() {
        return probability;
    }

    public boolean containsComputerAtIndex(int i) {
        return i < (realComputers.size() + honeyComputers.size());
    }

    public boolean computerAtIndexIsReal(int i) {
        return i < realComputers.size();
    }

    public boolean computerAtIndexIsHoney(int i) {
        return i >= realComputers.size() && i < (realComputers.size() + honeyComputers.size());
    }

    public Computer getComputerAtIndex(int i) {
        return (i < realComputers.size() ? realComputers.get(i) : honeyComputers.get(i - realComputers.size()));
    }

    public boolean portAtIndexInComputerExists(int computerI, int portI) {
        return containsComputerAtIndex(computerI) && getComputerAtIndex(computerI).containsPortAtIndex(portI);
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void addHoneyComputer(Computer computer) {
        honeyComputers.add(computer);
    }

    public HashSet<Integer> getOpenPortsInNetwork() {
        HashSet<Integer> openPortsInNetwork = new HashSet<>();
        for (Computer computer : realComputers) {
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

    private String createStringRepresentation() {
        StringBuilder sb = new StringBuilder("N{");
        for (int computerI = 0; computerI < realComputers.size(); ++computerI) {
            sb.append(realComputers.get(computerI).getStringRepresentation()).append(computerI < realComputers.size() - 1 ? "," : "");
        }
        for (int computerI = 0; computerI < honeyComputers.size(); ++computerI) {
            sb.append(",").append(honeyComputers.get(computerI).getStringRepresentation());
        }
        return sb.append("}").toString();
    }

    public String getStringRepresentation() {
        return createStringRepresentation();
    }

    @Override
    public String toString() {
        return "Network{" +
                "realComputers=" + realComputers +
                ", honeyComputers=" + honeyComputers +
                '}';
    }
}
