package networkproblem;

import helpers.HelperFunctions;
import pomdpproblem.POMDPProblem;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class NetworkDistrubitionToPOMDPConverter {
    private static final double DEFAULT_DISCOUNT = 0.9;
    private static final int DEFAULT_HONEYPOTS_COUNT = 2;
    private static final int DEFAULT_MAX_NUMBER_OF_DETECTED_ATTACKS_ALLOWED = 0;
    private static final double DEFAULT_SUCCESFUL_ATTACK_REWARD = 1.0;
    private static final double DEFAULT_PROBE_SUCCESS_PROBABILITY = 0.5;
    private static final double DEFAULT_PROBE_COST = DEFAULT_SUCCESFUL_ATTACK_REWARD / 2;

    private final String pathToNetworkFile;

    private LinkedList<Network> networks;

    private double discount;
    private int honeypotsCount;
    private int maxNumberOfDetectedAttacksAllowed;
    private double successfulAttackReward;
    private double probeSuccessProbability;
    private double probeCost;

    private POMDPProblem pomdpProblem;

    public NetworkDistrubitionToPOMDPConverter(String fileName) {
        pathToNetworkFile = fileName;
        networks = new LinkedList<>();

        discount = DEFAULT_DISCOUNT;
        honeypotsCount = DEFAULT_HONEYPOTS_COUNT;
        maxNumberOfDetectedAttacksAllowed = DEFAULT_MAX_NUMBER_OF_DETECTED_ATTACKS_ALLOWED;
        successfulAttackReward = DEFAULT_SUCCESFUL_ATTACK_REWARD;
        probeSuccessProbability = DEFAULT_PROBE_SUCCESS_PROBABILITY;
        probeCost = DEFAULT_PROBE_COST;

        pomdpProblem = null;
    }

    public POMDPProblem getPomdpProblem() {
        if (pomdpProblem == null) {
            createPomdpProblem();
        }
        return pomdpProblem;
    }

    public NetworkDistrubitionToPOMDPConverter setDiscount(double discount) {
        this.discount = discount;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setHoneypotsCount(int honeypotsCount) {
        this.honeypotsCount = honeypotsCount;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setMaxNumberOfDetectedAttacksAllowed(int maxNumberOfDetectedAttacksAllowed) {
        this.maxNumberOfDetectedAttacksAllowed = maxNumberOfDetectedAttacksAllowed;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setSuccessfulAttackReward(double successfulAttackReward) {
        this.successfulAttackReward = successfulAttackReward;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setProbeSuccessProbability(double probeSuccessProbability) {
        this.probeSuccessProbability = probeSuccessProbability;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setProbeCost(double probeCost) {
        this.probeCost = probeCost;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter loadNetwork() {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(pathToNetworkFile));
            readFile(bf);

        } catch (FileNotFoundException e) {
            System.err.println("File " + pathToNetworkFile + " does not exist");
            System.exit(20);
        } catch (IOException e) {
            System.err.println("Could not read from " + pathToNetworkFile);
            System.exit(30);
        }
        return this;
    }

    private void readFile(BufferedReader bf) throws IOException {
        String line;
        bf.readLine();
        int totalNetworksSum = 0;
        while ((line = bf.readLine()) != null) {
            networks.add(new Network(line));
            totalNetworksSum += networks.getLast().getProbability();
        }
        for (Network net : networks) {
            net.setProbability(net.getProbability() / totalNetworksSum);
        }
        System.out.println(networks);
    }

    private void createPomdpProblem() {
        System.out.println("Creating POMDP problem");
        ArrayList<Network> states = createStates();
        ArrayList<Action> actions = createActions(states);
        double[][][] transitionFunction = createTransitionFunction(states, actions);
    }

    private ArrayList<Network> createStates() {
        System.out.println("\tCreating states");
        HashSet<String> productionPortsSet = new HashSet<>();
        for (Network net : networks) {
            productionPortsSet.addAll(net.getOpenPortsInNetwork());
        }
        ArrayList<String> productionPorts = new ArrayList<>(productionPortsSet);
        int productionPortsCount = productionPorts.size();
        System.out.println("\t\tProduction ports: " + productionPorts);

        // now we can do only 1 or 2 honeypots
        if (honeypotsCount > 2 || honeypotsCount < 0) {
            System.err.println("\t\tCan't do that boss, only honepots <= 2");
            System.exit(100);
        }
        long virtualNetworksWith1ComputerCount = HelperFunctions.factorial(productionPortsCount) /
                (HelperFunctions.factorial(honeypotsCount) * HelperFunctions.factorial(productionPortsCount - honeypotsCount));
        long virtualNetworksWith2ComputersCount = HelperFunctions.factorial(productionPortsCount - 1 + honeypotsCount) /
                (HelperFunctions.factorial(honeypotsCount) * HelperFunctions.factorial(productionPortsCount - 1));
        int virtualNetworksCount = (int) (virtualNetworksWith1ComputerCount + virtualNetworksWith2ComputersCount);
        int statesCount = networks.size() * virtualNetworksCount;

        System.out.println();
        System.out.println("\t\tNumber of input networks: " + networks.size());
        System.out.println("\t\tNumber of virtual networks combinations: " + virtualNetworksCount);
        System.out.println("\t\tTotal number of POMDP states: " + statesCount);

        ArrayList<Network> states = new ArrayList<>(statesCount);

        Network state;
        String[] portsComb;
        // #virtual_computers = 1
        portsComb = new String[honeypotsCount];
        for (Network inputNetwork : networks) {
            for (int port1 = 0; port1 < productionPortsCount; ++port1) {
                for (int port2 = port1 + 1; port2 < productionPortsCount; ++port2) {
                    portsComb[0] = productionPorts.get(port1);
                    portsComb[1] = productionPorts.get(port2);
                    state = new Network(inputNetwork);
                    state.addHoneyComputer(new Computer(false, portsComb));
                    states.add(state);
                }
            }
        }

        // #virtual_computers = 2
        portsComb = new String[1];
        if (honeypotsCount >= 2) {
            for (Network inputNetwork : networks) {
                for (int port1 = 0; port1 < productionPortsCount; ++port1) {
                    for (int port2 = port1; port2 < productionPortsCount; ++port2) {
                        state = new Network(inputNetwork);
                        portsComb[0] = productionPorts.get(port1);
                        state.addHoneyComputer(new Computer(false, portsComb));
                        portsComb[0] = productionPorts.get(port2);
                        state.addHoneyComputer(new Computer(false, portsComb));
                        states.add(state);
                    }
                }
            }
        }

        return states;
    }

    private ArrayList<Action> createActions(ArrayList<Network> states) {
        System.out.println("\tCreating actions");

        int maxComputersCount = Integer.MIN_VALUE;
        int maxPortsCount = Integer.MIN_VALUE;
        for (Network state : states) {
            maxComputersCount =
                    Math.max(maxComputersCount, state.getRealComputers().size() + state.getHoneyComputers().size());
            for (Computer computer : state.getRealComputers()) {
                maxPortsCount = Math.max(maxPortsCount, computer.getPorts().size());
            }
        }
        maxPortsCount = Math.max(maxPortsCount, honeypotsCount);

        int actionsCount = maxComputersCount * maxPortsCount * 2;
        ArrayList<Action> actions = new ArrayList<>(actionsCount);
        for (int targetComputerI = 0; targetComputerI < maxComputersCount; ++targetComputerI) {
            for (int targetPortI = 0; targetPortI < maxPortsCount; ++targetPortI) {
                actions.add(new Action("probe", String.valueOf(targetComputerI), String.valueOf(targetPortI)));
                actions.add(new Action("attack", String.valueOf(targetComputerI), String.valueOf(targetPortI)));
            }
        }

        for (Action action : actions) {
            System.out.println(action);
        }

        return actions;
    }

    private double[][][] createTransitionFunction(ArrayList<Network> states, ArrayList<Action> actions) {
        System.out.println("\tCreating transition function");

        int maxComputersCount = Integer.MIN_VALUE;
        for (Network state : states) {
            maxComputersCount =
                    Math.max(maxComputersCount, state.getRealComputers().size() + state.getHoneyComputers().size());
        }

        System.out.println("\t\tMax number of computers in a single network: " + maxComputersCount);

        return null;
    }
}
