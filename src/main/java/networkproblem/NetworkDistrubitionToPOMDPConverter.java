package networkproblem;

import helpers.HelperFunctions;
import pomdpproblem.POMDPProblem;

import java.io.*;
import java.util.*;

public class NetworkDistrubitionToPOMDPConverter {
    private static final double DEFAULT_DISCOUNT = 0.9;
    private static final int DEFAULT_HONEYPOTS_COUNT = 2;
    private static final int DEFAULT_MAX_NUMBER_OF_DETECTED_ATTACKS_ALLOWED = 0;
    private static final double DEFAULT_SUCCESSFUL_ATTACK_PROBABILITY = 1.0;
    private static final double DEFAULT_SUCCESFUL_ATTACK_REWARD = 1.0;
    private static final double DEFAULT_PROBE_SUCCESS_PROBABILITY = 0.75;
    private static final double DEFAULT_PROBE_COST = DEFAULT_SUCCESFUL_ATTACK_REWARD / 2;

    private final String pathToNetworkFile;

    private ArrayList<Network> networks;
    private TreeSet<Integer> openPorts;

    private HashMap<Integer, Double> portsValues;
    private HashMap<Integer, Double> portsSuccessfulAttackProbs;
    private double discount;
    private int honeypotsCount;
    private int maxNumberOfDetectedAttacksAllowed;
    private double getDefaultSuccessfulAttackProbability;
    private double defaultSuccessfulAttackReward;
    private double probeSuccessProbability;
    private double probeCost;

    private POMDPProblem pomdpProblem;
    private ArrayList<LinkedList<Integer>> groups;
    private double[] groupsProbabilities;

    public NetworkDistrubitionToPOMDPConverter(String fileName) {
        pathToNetworkFile = fileName;
        try {
            networks = new ArrayList<>(HelperFunctions.countLinesInFile(pathToNetworkFile) - 1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(132);
        }
        openPorts = new TreeSet<>();

        portsValues = null;
        portsSuccessfulAttackProbs = null;
        discount = DEFAULT_DISCOUNT;
        honeypotsCount = DEFAULT_HONEYPOTS_COUNT;
        maxNumberOfDetectedAttacksAllowed = DEFAULT_MAX_NUMBER_OF_DETECTED_ATTACKS_ALLOWED;
        getDefaultSuccessfulAttackProbability = DEFAULT_SUCCESSFUL_ATTACK_PROBABILITY;
        defaultSuccessfulAttackReward = DEFAULT_SUCCESFUL_ATTACK_REWARD;
        probeSuccessProbability = DEFAULT_PROBE_SUCCESS_PROBABILITY;
        probeCost = DEFAULT_PROBE_COST;


        pomdpProblem = null;
        groups = null;
        groupsProbabilities = null;

        loadNetwork();
    }

    private void loadNetwork() {
        try {
            readNetworksFile();
            System.out.println("Read network: " + networks);
        } catch (IOException e) {
            System.err.println("Could not read from " + pathToNetworkFile);
            System.exit(30);
        }
    }

    private void readNetworksFile() throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(pathToNetworkFile));
        bf.readLine();
        String line;
        Network network;
        int totalNetworksSum = 0;
        while ((line = bf.readLine()) != null) {
            network = new Network(line);
            networks.add(network);
            totalNetworksSum += network.getProbability();
        }
        bf.close();
        groups = new ArrayList<>(networks.size());
        groupsProbabilities = new double[networks.size()];
        for (int networkI = 0; networkI < networks.size(); ++networkI) {
            networks.get(networkI).setProbability(networks.get(networkI).getProbability() / totalNetworksSum);
            groupsProbabilities[networkI] = networks.get(networkI).getProbability();
            groups.add(new LinkedList<>());
            openPorts.addAll(networks.get(networkI).getOpenPortsInNetwork());
        }
    }

    public POMDPProblem getPomdpProblem() {
        if (pomdpProblem == null) {
            createPomdpProblem();
        }
        return pomdpProblem;
    }

    public ArrayList<LinkedList<Integer>> getStatesGroupsIds() {
        return groups;
    }

    public double[] getGroupsProbabilities() {
        return groupsProbabilities;
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

    public NetworkDistrubitionToPOMDPConverter setDefaultSuccessfulAttackReward(double successfulAttackReward) {
        this.defaultSuccessfulAttackReward = successfulAttackReward;
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter setDefaultSuccessfulAttackProbability(double getDefaultSuccessfulAttackProbability) {
        this.getDefaultSuccessfulAttackProbability = getDefaultSuccessfulAttackProbability;
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

    public NetworkDistrubitionToPOMDPConverter loadPortsValues(String portsValuesFileName) {
        portsValues = readPortsAndInfoInMap(portsValuesFileName);
        return this;
    }

    public NetworkDistrubitionToPOMDPConverter loadPortsSuccessfulAttackProbs(String portsSuccessfulAttackProbsFileName) {
        portsSuccessfulAttackProbs = readPortsAndInfoInMap(portsSuccessfulAttackProbsFileName);
        return this;
    }

    private HashMap<Integer, Double> readPortsAndInfoInMap(String fileName) {
        HashMap<Integer, Double> map = new HashMap<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            bf.readLine();
            String line;
            String[] lineSplits;
            while ((line = bf.readLine()) != null) {
                lineSplits = line.split(",");
                map.put(Integer.valueOf(lineSplits[0]), Double.valueOf(lineSplits[1]));
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(30);
        }
        return map;
    }

    private void createPomdpProblem() {
        System.out.println("Creating POMDP problem");
        ArrayList<State> states = createStates();
        ArrayList<String> statesNames = createStatesNames(states);
        HashMap<String, Integer> statesToIndexes = createStateToIndexMap(statesNames);
        ArrayList<Action> actions = createActions(states);
        ArrayList<String> actionsNames = creatActionsNames(actions);
        HashMap<String, Integer> actionsToIndexes = createActionToIndexMap(actionsNames);
        double[][][] transitionFunction = createTransitionFunction(states, actions);
        ArrayList<String> observations = createObservations();
        HashMap<String, Integer> observationsToIndexes = createObservationToIndexMap(observations);
        double[][][] observationProbabilities = createObservationProbabilities(states, actions, observationsToIndexes);
        double[][][][] rewards = createRewardFunction(states, actions, observationsToIndexes);
        pomdpProblem = new POMDPProblem(statesNames, statesToIndexes,
                actionsNames, actionsToIndexes, transitionFunction,
                observations, observationsToIndexes, observationProbabilities,
                rewards,
                discount);
    }

    private ArrayList<State> createStates() {
        System.out.println("\tCreating states");
        HashSet<Integer> productionPortsSet = new HashSet<>();
        for (Network net : networks) {
            productionPortsSet.addAll(net.getOpenPortsInNetwork());
        }
        ArrayList<Integer> productionPorts = new ArrayList<>(productionPortsSet);
        int productionPortsCount = productionPorts.size();
        System.out.println("\t\tProduction ports: " + productionPorts);

        // TODO now we can do only honeypotsCount == 1 || honeypotsCount == 2
        if (honeypotsCount > 2 || honeypotsCount < 0) {
            System.err.println("\t\tCan't do that boss, only honepots <= 2");
            System.exit(100);
        }
        // TODO now we can do only maxNumberOfDetectedAttacksAllowed == 0
        if (maxNumberOfDetectedAttacksAllowed > 0) {
            System.err.println("\t\tNope, can't do that, maxNumberOfDetectedAttacksAllowed must be 0");
            System.exit(13223);
        }
        long virtualNetworksWith1ComputerCount = HelperFunctions.factorial(productionPortsCount) /
                (HelperFunctions.factorial(honeypotsCount) * HelperFunctions.factorial(productionPortsCount - honeypotsCount));
        long virtualNetworksWith2ComputersCount = 0;
        if (honeypotsCount > 1) {
            virtualNetworksWith2ComputersCount = HelperFunctions.factorial(productionPortsCount - 1 + honeypotsCount) /
                    (HelperFunctions.factorial(honeypotsCount) * HelperFunctions.factorial(productionPortsCount - 1));
        }
        int virtualNetworksCount = (int) (virtualNetworksWith1ComputerCount + virtualNetworksWith2ComputersCount);
        int statesCount = networks.size() * virtualNetworksCount + 1;

        System.out.println();
        System.out.println("\t\tNumber of input networks: " + networks.size());
        System.out.println("\t\tNumber of virtual networks combinations: " + virtualNetworksCount);
        System.out.println("\t\tTotal number of POMDP states: " + statesCount);

        ArrayList<State> states = new ArrayList<>(statesCount);

        Network network;
        int[] portsComb;
        if (honeypotsCount == 1) {
            portsComb = new int[1];
            for (int inputNetworkI = 0; inputNetworkI < networks.size(); ++inputNetworkI) {
                for (int port = 0; port < productionPortsCount; ++port) {
                    portsComb[0] = productionPorts.get(port);
                    network = new Network(networks.get(inputNetworkI));
                    network.addComputer(new Computer(false, portsComb));
                    groups.get(inputNetworkI).add(states.size());
                    states.add(new State(network));
                }
            }
        } else if (honeypotsCount == 2) {
            // #virtual_computers = 1
            portsComb = new int[2];
            for (int inputNetworkI = 0; inputNetworkI < networks.size(); ++inputNetworkI) {
                for (int port1 = 0; port1 < productionPortsCount; ++port1) {
                    for (int port2 = port1 + 1; port2 < productionPortsCount; ++port2) {
                        portsComb[0] = productionPorts.get(port1);
                        portsComb[1] = productionPorts.get(port2);
                        network = new Network(networks.get(inputNetworkI));
                        network.addComputer(new Computer(false, portsComb));
                        groups.get(inputNetworkI).add(states.size());
                        states.add(new State(network));
                    }
                }
            }

            // #virtual_computers = 2
            portsComb = new int[1];
            if (honeypotsCount >= 2) {
                for (int inputNetworkI = 0; inputNetworkI < networks.size(); ++inputNetworkI) {
                    for (int port1 = 0; port1 < productionPortsCount; ++port1) {
                        for (int port2 = port1; port2 < productionPortsCount; ++port2) {
                            network = new Network(networks.get(inputNetworkI));
                            portsComb[0] = productionPorts.get(port1);
                            network.addComputer(new Computer(false, portsComb));
                            portsComb[0] = productionPorts.get(port2);
                            network.addComputer(new Computer(false, portsComb));
                            groups.get(inputNetworkI).add(states.size());
                            states.add(new State(network));
                        }
                    }
                }
            }
        }

        // add final state
        states.add(new State());
        return states;
    }

    private ArrayList<String> createStatesNames(ArrayList<State> states) {
        ArrayList<String> statesNames = new ArrayList<>(states.size());
        for (State state : states) {
            statesNames.add(state.getName());
        }
        return statesNames;
    }

    private HashMap<String, Integer> createStateToIndexMap(ArrayList<String> statesNames) {
        System.out.println("\tCreating states names");
        HashMap<String, Integer> stateNamesToIndex = new HashMap<>(statesNames.size() * 2);
        for (int s = 0; s < statesNames.size(); ++s) {
            System.out.println("\t\t" + s + ": " + statesNames.get(s));
            stateNamesToIndex.put(statesNames.get(s), s);
        }
        return stateNamesToIndex;
    }

    private ArrayList<Action> createActions(ArrayList<State> states) {
        System.out.println("\tCreating actions");

        int maxComputersCount = Integer.MIN_VALUE;
        for (State state : states) {
            if (state.isFinalState()) {
                continue;
            }
            maxComputersCount =
                    Math.max(maxComputersCount, state.getNetwork().getComputers().size());
        }

        int actionsCount = maxComputersCount * openPorts.size() * 2;

        System.out.println("\t\tMax computers count in a single network: " + maxComputersCount);
        System.out.println("\t\tMax ports count in a single computer: " + openPorts.size());
        System.out.println("\t\tTotal number of probe/attack actions: " + actionsCount);
        ArrayList<Action> actions = new ArrayList<>(actionsCount);
        for (int targetComputerI = 0; targetComputerI < maxComputersCount; ++targetComputerI) {
            for (Integer port : openPorts) {
                actions.add(new Action(Action.ActionType.PROBE, targetComputerI, port));
                actions.add(new Action(Action.ActionType.ATTACK, targetComputerI, port));
            }
        }
        return actions;
    }

    private ArrayList<String> creatActionsNames(ArrayList<Action> actions) {
        ArrayList<String> actionsNames = new ArrayList<>(actions.size());
        for (Action action : actions) {
            actionsNames.add(action.getName());
        }
        return actionsNames;
    }

    private HashMap<String, Integer> createActionToIndexMap(ArrayList<String> actionsNames) {
        System.out.println("\tCreating actions names");
        HashMap<String, Integer> actionNamesToIndex = new HashMap<>(actionsNames.size() * 2);
        for (int a = 0; a < actionsNames.size(); ++a) {
            System.out.println("\t\t" + a + ": " + actionsNames.get(a));
            actionNamesToIndex.put(actionsNames.get(a), a);
        }
        return actionNamesToIndex;
    }

    private double[][][] createTransitionFunction(ArrayList<State> states, ArrayList<Action> actions) {
        System.out.println("\tCreating transition function");

        double[][][] transitionFunction = new double[actions.size()][states.size()][states.size()]; // T(a,s,s_)

        int finalS = states.size() - 1;
        Action action;
        State state;
        Network network;
        for (int a = 0; a < actions.size(); ++a) {
            // cannnot go anywhere from final state
            transitionFunction[a][finalS][finalS] = 1.0;
            action = actions.get(a);
            for (int s = 0; s < finalS; ++s) {
                state = states.get(s);
                network = state.getNetwork();
                switch (action.getActionType()) {
                    case PROBE:
                        transitionFunction[a][s][s] = 1.0;
                        break;
                    case ATTACK:
                        // does the computer and the port we attack even exist at this index in this network?
                        if (network.containsComputerAtIndex(action.getTargetComputerI()) &&
                                network.getComputerAtIndex(action.getTargetComputerI()).containsPort(action.getTargetPort())) {
                            if (network.computerAtIndexIsReal(action.getTargetComputerI())) {
                                transitionFunction[a][s][s] = 1.0;
                            } else {
                                // attack on a honeypot
                                if (state.getNumberOfAttackOnHoneypot() + 1 <= maxNumberOfDetectedAttacksAllowed) {
                                    // TODO add transition to the state with honeypot attacks higher by one
                                    System.exit(12345);
                                } else {
                                    transitionFunction[a][s][finalS] = 1.0;
                                }
                            }
                        } else {
                            transitionFunction[a][s][s] = 1.0;
                        }
                        break;
                    default:
                        System.out.println("No such action");
                        System.exit(21313);
                }
            }
        }

        /*
        for (int a = 0; a < actions.size(); ++a) {
            System.out.println("\t\t" + actions.get(a));
            for (int s = 0; s < states.size(); ++s) {
                System.out.print("\t\t");
                for (int s_ = 0; s_ < states.size(); ++s_) {
                    System.out.print(transitionFunction[a][s][s_] + " ");
                }
                System.out.println();
            }
        }
        */

        return transitionFunction;
    }

    private ArrayList<String> createObservations() {
        System.out.println("\tCreating observations");
        ArrayList<String> observations = new ArrayList<>(Arrays.asList(Observation.ObservationType.NOTHING.toString(),
                Observation.ObservationType.REAL.toString(),
                Observation.ObservationType.HONEYPOT.toString()));
        System.out.println("\t\tObservations: " + observations);
        return observations;
    }

    private HashMap<String, Integer> createObservationToIndexMap(ArrayList<String> observations) {
        HashMap<String, Integer> observationToIndex = new HashMap<>(observations.size() * 2);
        for (int o = 0; o < observations.size(); ++o) {
            observationToIndex.put(observations.get(o), o);
        }
        return observationToIndex;
    }

    private double[][][] createObservationProbabilities(ArrayList<State> states,
                                                        ArrayList<Action> actions,
                                                        HashMap<String, Integer> observationToIndex) {
        System.out.println("\tCreating observation probabilities");
        double[][][] observationProbabilities = new double[actions.size()][states.size()][observationToIndex.size()];
        int nothingObsI = observationToIndex.get(Observation.ObservationType.NOTHING.toString());
        int realObsI = observationToIndex.get(Observation.ObservationType.REAL.toString());
        int honeypotObsI = observationToIndex.get(Observation.ObservationType.HONEYPOT.toString());

        int finalS = states.size() - 1;
        Action action;
        State state;
        Network network;
        for (int a = 0; a < actions.size(); ++a) {
            action = actions.get(a);
            for (int s_ = 0; s_ < finalS; ++s_) {
                state = states.get(s_);
                network = state.getNetwork();
                switch (action.getActionType()) {
                    case PROBE:
                        if (network.containsComputerAtIndex(action.getTargetComputerI()) &&
                                network.getComputerAtIndex(action.getTargetComputerI()).containsPort(action.getTargetPort())) {
                            if (network.getComputerAtIndex(action.getTargetComputerI()).isReal()) {
                                // TODO probe success probability for real computers, too?
                                observationProbabilities[a][s_][realObsI] = 1.0;
                            } else {
                                observationProbabilities[a][s_][honeypotObsI] = probeSuccessProbability;
                                observationProbabilities[a][s_][realObsI] = 1.0 - probeSuccessProbability;
                            }
                        } else {
                            observationProbabilities[a][s_][nothingObsI] = 1.0;
                        }
                        break;
                    case ATTACK:
                        // does the computer and the port we attack even exist at this index in this network?
                        if (network.containsComputerAtIndex(action.getTargetComputerI()) &&
                                network.getComputerAtIndex(action.getTargetComputerI()).containsPort(action.getTargetPort())) {
                            if (network.computerAtIndexIsReal(action.getTargetComputerI())) {
                                // TODO attack real computer - what you should see
                                observationProbabilities[a][s_][realObsI] = 1.0;
                            } else { // attack on a honeypot
                                // TODO attacked honeypot - what you should see
                                observationProbabilities[a][s_][honeypotObsI] = 1.0;
                                if (state.getNumberOfAttackOnHoneypot() + 1 <= maxNumberOfDetectedAttacksAllowed) {
                                    System.exit(12345);
                                } else { // you attacked honeypot for the last time...
                                }
                            }
                        } else {
                            observationProbabilities[a][s_][nothingObsI] = 1.0;
                        }
                        break;
                    default:
                        System.out.println("No such action");
                        System.exit(21313);
                }
            }
        }

        // cannot see anything from final state
        for (int a = 0; a < actions.size(); ++a) {
            observationProbabilities[a][finalS][nothingObsI] = 1.0;
        }

        /*
        for (int s_ = 0; s_ < states.size(); ++s_) {
            System.out.println("\t\t" + states.get(s_));
            for (int a = 0; a < actions.size(); ++a) {
                System.out.print("\t\t");
                for (int o = 0; o < observationToIndex.size(); ++o) {
                    System.out.print(observationProbabilities[a][s_][o] + " ");
                }
                System.out.println();
            }
        }
        */

        return observationProbabilities;
    }

    private double[][][][] createRewardFunction(ArrayList<State> states,
                                                ArrayList<Action> actions,
                                                HashMap<String, Integer> observationToIndex) {
        System.out.println("\tCreating reward function");
        double[][][][] rewards = new double[actions.size()][states.size()][states.size()][observationToIndex.size()];

        int realObsI = observationToIndex.get(Observation.ObservationType.REAL.toString());

        int finalS = states.size() - 1;
        Action action;
        State state;
        Network network;
        int computerI, port;
        double rewardForAttack, rewardForSuccefulAttack, successfulAttackProb;
        for (int a = 0; a < actions.size(); ++a) {
            action = actions.get(a);
            System.out.println("\t\t" + action);
            for (int s = 0; s < finalS; ++s) {
                state = states.get(s);
                network = state.getNetwork();
                switch (action.getActionType()) {
                    case PROBE:
                        for (int o = 0; o < observationToIndex.size(); ++o) {
                            rewards[a][s][s][o] = probeCost;
                        }
                        break;
                    case ATTACK:
                        // does the computer and the port we attack even exist at this index in this network?
                        // TODO reward for succesful attack on real computer/port, but what about loss?
                        // TODO ^^ do we need observation detected?
                        computerI = action.getTargetComputerI();
                        port = action.getTargetPort();
                        if (network.containsComputerAtIndex(computerI) &&
                                network.getComputerAtIndex(computerI).containsPort(port)) {
                                rewardForSuccefulAttack = (portsValues != null && portsValues.containsKey(port) ?
                                        portsValues.get(port) : defaultSuccessfulAttackReward);
                                successfulAttackProb = (portsSuccessfulAttackProbs != null &&
                                        portsSuccessfulAttackProbs.containsKey(port) ?
                                        portsSuccessfulAttackProbs.get(port) : getDefaultSuccessfulAttackProbability);
                                rewardForAttack = successfulAttackProb * rewardForSuccefulAttack;
                                rewards[a][s][s][realObsI] = rewardForAttack;
                            System.out.println("\t\t\tr[" + a + "][" + s  + "][" + s  + "][" + realObsI + "] = " + rewards[a][s][s][realObsI]);
                        }
                        break;
                    default:
                        System.out.println("No such action");
                        System.exit(21313);
                }
            }
        }

        return rewards;
    }
}
