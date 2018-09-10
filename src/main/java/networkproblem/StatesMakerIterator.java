package networkproblem;

import helpers.HelperFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class StatesMakerIterator implements Iterator<State> {

    private final ArrayList<Network> inputNetworks;
    private final ArrayList<Integer> openPorts;
    private final int honeypotsCount;

    private final LinkedList<ArrayList<Integer>> honeycomputersSizesCombinations;
    private final ArrayList<ArrayList<int[]>> honeypotCombinations;

    private int currentInputNetworkI;
    private int currentCombinationI;

    public StatesMakerIterator(ArrayList<Network> inputNetworks, ArrayList<Integer> openPorts, int honeypotsCount) {
        this.inputNetworks = inputNetworks;
        this.openPorts = openPorts;
        this.honeypotsCount = honeypotsCount;

        honeycomputersSizesCombinations = new LinkedList<>();
        honeypotCombinations = new ArrayList<>(2 * (int)(HelperFunctions.factorial(openPorts.size()) / HelperFunctions.factorial(honeypotsCount)));

        createHoneypotCombinations();

        currentInputNetworkI = 0;
        currentCombinationI = 0;
    }

    @Override
    public boolean hasNext() {
        return currentInputNetworkI < inputNetworks.size();
    }

    @Override
    public State next() {
        Network network = new Network(inputNetworks.get(currentInputNetworkI));
        for (int[] honeycomputer : honeypotCombinations.get(currentCombinationI)) {
            network.addComputer(new Computer(false, honeycomputer));
        }
        ++currentCombinationI;
        if (currentCombinationI >= honeypotCombinations.size()) {
            ++currentInputNetworkI;
            currentCombinationI = 0;
        }
        return new State(network);
    }

    public int getTotalNumberOfStates() {
        return honeypotCombinations.size() * inputNetworks.size();
    }

    private void createHoneypotCombinations() {
        createPossibleHoneycomputersSizeCombinations(new ArrayList<>(honeypotsCount), 0, 1);
        System.out.println("Possible honeycomputers sizes combinations count: " + honeycomputersSizesCombinations.size());

        createHoneypotsCombinationsOfPossibleSizes();
        System.out.println("Honeypot combinations size: " + honeypotCombinations.size());
    }

    private void createHoneypotsCombinationsOfPossibleSizes() {
        for (ArrayList<Integer> honeycomputerSizesCombination : honeycomputersSizesCombinations) {
            System.out.println(honeycomputerSizesCombination);
            createHoneypotsCombinationsOfSizes(new ArrayList<>(honeycomputerSizesCombination.size()),
                    honeycomputerSizesCombination, 0);
        }
    }

    private void createHoneypotsCombinationsOfSizes(ArrayList<int[]> acum, ArrayList<Integer> honeycomputerSizes, int currentComputerI) {
        if (currentComputerI >= honeycomputerSizes.size()) {
            System.out.println("\t\t\t+" + acum.stream().map(x -> Arrays.toString(x)).collect(Collectors.joining(" | ")));
            honeypotCombinations.add(new ArrayList<>(acum));
            return;
        }
        int[] lastHoneyComputerNew = (currentComputerI == 0 ? null : acum.get(acum.size() - 1));
        int[] honeycomputer = initHoneycomputer(lastHoneyComputerNew, honeycomputerSizes.get(currentComputerI));
        if (honeycomputer == null) {
            return;
        }
        acum.add(honeycomputer);
        createHoneypotsCombinationsOfSizes(acum, honeycomputerSizes, currentComputerI + 1);
        acum.remove(acum.size() - 1);
        while (incrementPortInHoneycomputer(honeycomputer)) {
            acum.add(honeycomputer);
            createHoneypotsCombinationsOfSizes(acum, honeycomputerSizes, currentComputerI + 1);
            acum.remove(acum.size() - 1);
        }
    }

    private int[] initHoneycomputer(int[] lastHoneycomputer, int size) {
        if (size > openPorts.size()) {
            return null;
        }
        int[] honeycomputer = new int[size];
        if (lastHoneycomputer == null || lastHoneycomputer.length != size) {
            for (int portI = 0; portI < size; ++portI) {
                honeycomputer[portI] = portI;
            }
        } else {
            for (int portI = 0; portI < size; ++portI) {
                honeycomputer[portI] = lastHoneycomputer[portI];
            }
        }
        return honeycomputer;
    }

    private boolean incrementPortInHoneycomputer(int[] honeycomputer) {
        System.out.println("\tIncrementing " + Arrays.toString(honeycomputer));
        for (int portI = honeycomputer.length - 1; portI >= 0; --portI) {
            if (honeycomputer[portI] + 1 < openPorts.size()) {
                System.out.println("\t\tCan increment at index: " + portI);
                int toSumBase = honeycomputer[portI]+ 1;
                if (toSumBase + (honeycomputer.length - portI - 1) >= openPorts.size()) {
                    continue;
                }
                for (int off = 0; portI + off < honeycomputer.length; ++off) {
                    honeycomputer[portI + off] = toSumBase + off;
                    System.out.println("\t\tAfter increment: " + Arrays.toString(honeycomputer));
                }
                return true;
            }
        }
        return false;
    }

    private void createHoneyComputer(int honeycomputerSize, int[] lastHoneycomputer) {

    }

    private void createHoneyports(int honeycomputerSize, int honeyportI, int minPortI) {

    }

    private void createPossibleHoneycomputersSizeCombinations(ArrayList<Integer> acum, int currSum, int minSize) {
        if (currSum + minSize > honeypotsCount) {
            if (currSum == honeypotsCount) {
                honeycomputersSizesCombinations.add(new ArrayList<>(acum));
            }
            return;
        }
        for (int honeycomputerSize = minSize; currSum + honeycomputerSize <= honeypotsCount; ++honeycomputerSize) {
            acum.add(honeycomputerSize);
            createPossibleHoneycomputersSizeCombinations(acum, currSum + honeycomputerSize, honeycomputerSize);
            acum.remove(acum.size() - 1);
        }
    }
}
