package networkproblem;

import java.util.ArrayList;
import java.util.Arrays;

public class Network {

    private static final String VALUES_DELIM = ",";
    private static final String COMPUTERS_DELIM = "\\|";

    private final double probability;
    private final ArrayList<Computer> computers;

    public Network(String line) {
        String[] lineSplits = line.split(VALUES_DELIM, 2);
        probability = Double.parseDouble(lineSplits[0]);
        computers = parseNetworkString(lineSplits[1]);
    }

    public double getProbability() {
        return probability;
    }

    public ArrayList<Computer> getComputers() {
        return computers;
    }

    private ArrayList<Computer> parseNetworkString(String networkString) {
        String[] computersStrings = networkString.split(COMPUTERS_DELIM);
        System.out.println(Arrays.toString(computersStrings));
        ArrayList<Computer> computersList = new ArrayList<>(computersStrings.length);
        for (int i = 0; i < computersStrings.length; ++i) {
            computersList.add(new Computer(computersStrings[i]));
        }
        return computersList;
    }

    @Override
    public String toString() {
        return "Network{" +
                "probability=" + probability +
                ", computers=" + computers +
                '}';
    }
}
