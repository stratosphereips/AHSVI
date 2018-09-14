package networkproblem.statesmaker;

import networkproblem.Computer;
import networkproblem.Network;
import networkproblem.State;

import java.util.*;

public class StatesMakerIterator implements Iterator<State> {

    private final ArrayList<Network> inputNetworks;
    private final ArrayList<Integer> openPorts;
    private final int honeypotsCount;

    private final LinkedList<ArrayList<Integer>> honeycomputersSizesCombinations;
    private final LinkedList<ArrayList<Computer>> honeycomputersCombinations;


    private CombinationsPermutationsMaker permutationsMaker;
    private ListIterator<ArrayList<Computer>> combinationsIter;
    private int currentInputNetworkI;

    public StatesMakerIterator(ArrayList<Network> inputNetworks, ArrayList<Integer> openPorts, int honeypotsCount) {
        this.inputNetworks = inputNetworks;
        this.openPorts = openPorts;
        this.honeypotsCount = honeypotsCount;

        honeycomputersSizesCombinations = new LinkedList<>();
        honeycomputersCombinations = new LinkedList<>();

        createHoneypotCombinations();

        permutationsMaker = null;
        combinationsIter = honeycomputersCombinations.listIterator();
        currentInputNetworkI = 0;
    }

    @Override
    public boolean hasNext() {
        return currentInputNetworkI < inputNetworks.size();
    }

    @Override
    public State next() {
        Network originalNetwork = inputNetworks.get(currentInputNetworkI);
        if (permutationsMaker == null || !permutationsMaker.hasNext()) {
            ArrayList<Computer> network = new ArrayList<>(originalNetwork.getComputers());
            ArrayList<Computer> honeycomputersCombination = combinationsIter.next();
            network.addAll(honeycomputersCombination);
            permutationsMaker = new AllPermutationsMaker(network);
            if (!combinationsIter.hasNext()) {
                ++currentInputNetworkI;
                combinationsIter = honeycomputersCombinations.listIterator();
            }
        }
        ArrayList<Computer> networkPermutation = permutationsMaker.next();
        return new State(new Network(originalNetwork.getGroupId(), originalNetwork.getProbability(), networkPermutation));
    }

    public int getTotalNumberOfStates() {
        return honeycomputersCombinations.size() * inputNetworks.size();
    }

    private void createHoneypotCombinations() {
        createPossibleHoneycomputersSizeCombinations(new ArrayList<>(honeypotsCount), 0, 1);
        System.out.println("Possible honeycomputers sizes combinations count: " + honeycomputersSizesCombinations.size());

        createHoneypotsCombinationsOfPossibleSizes();
        System.out.println("Honeypot combinations size: " + honeycomputersCombinations.size());

    }

    private ArrayList<Computer> transformPortIndexesCombinationsToHoneycomputer(ArrayList<int[]> portIndexesCombination) {
        ArrayList<Computer> combination = new ArrayList<>(portIndexesCombination.size());
        int[] ports;
        for (int[] honeycomputerPortIndexes : portIndexesCombination) {
            ports = new int[honeycomputerPortIndexes.length];
            for (int portI = 0; portI < honeycomputerPortIndexes.length; ++portI) {
                ports[portI] = openPorts.get(honeycomputerPortIndexes[portI]);
            }
            combination.add(new Computer(false, ports));
        }
        //System.out.println("\t\t+" + combination.stream().map(x->Arrays.toString(x)).collect(Collectors.joining(" | ")));
        return combination;
    }

    private void createHoneypotsCombinationsOfPossibleSizes() {
        for (ArrayList<Integer> honeycomputerSizesCombination : honeycomputersSizesCombinations) {
            //System.out.println(honeycomputerSizesCombination);
            createHoneypotsCombinationsOfSizes(new ArrayList<>(honeycomputerSizesCombination.size()),
                    honeycomputerSizesCombination, 0);
        }
    }

    private void createHoneypotsCombinationsOfSizes(ArrayList<int[]> acum, ArrayList<Integer> honeycomputerSizes, int currentComputerI) {
        if (currentComputerI >= honeycomputerSizes.size()) {
            //System.out.println("\t\t\t+" + acum.stream().map(x -> Arrays.toString(x)).collect(Collectors.joining(" | ")));
            honeycomputersCombinations.add(transformPortIndexesCombinationsToHoneycomputer(acum));
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
        //System.out.println("\tIncrementing " + Arrays.toString(honeycomputer));
        for (int portI = honeycomputer.length - 1; portI >= 0; --portI) {
            if (honeycomputer[portI] + 1 < openPorts.size()) {
                //System.out.println("\t\tCan increment at index: " + portI);
                int toSumBase = honeycomputer[portI] + 1;
                if (toSumBase + (honeycomputer.length - portI - 1) >= openPorts.size()) {
                    continue;
                }
                for (int off = 0; portI + off < honeycomputer.length; ++off) {
                    honeycomputer[portI + off] = toSumBase + off;
                    //System.out.println("\t\tAfter increment: " + Arrays.toString(honeycomputer));
                }
                return true;
            }
        }
        return false;
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
