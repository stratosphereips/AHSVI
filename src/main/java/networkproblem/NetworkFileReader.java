package networkproblem;

import pomdpproblem.POMDPProblem;

import java.io.*;
import java.util.LinkedList;

public class NetworkFileReader {

    private final String pathToNetworkFile;
    private POMDPProblem pomdpProblem;

    private static final char VALUES_DELIM = ',';

    public NetworkFileReader(String fileName) {
        pathToNetworkFile = fileName;
        pomdpProblem = null;
    }

    public POMDPProblem getPomdpProblem() {
        return pomdpProblem;
    }

    public void loadNetwork() {
        POMDPProblem loadedPomdpProblem = null;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(pathToNetworkFile));
            LinkedList<Network> networks = readFile(bf);

        } catch (FileNotFoundException e) {
            System.err.println("File " + pathToNetworkFile + " does not exist");
            System.exit(20);
        } catch (IOException e) {
            System.err.println("Could not read from " + pathToNetworkFile);
            System.exit(30);
        } finally {
            pomdpProblem = loadedPomdpProblem;
        }
    }

    private LinkedList<Network> readFile(BufferedReader bf) throws IOException {
        String line;
        LinkedList<Network> networks = new LinkedList<>();
        bf.readLine();
        while ((line = bf.readLine()) != null) {
            System.out.println("Read    " + line);
            networks.add(new Network(line));
        }
        System.out.println(networks);
        return networks;
    }

    private POMDPProblem createPomdpProblem(LinkedList<Network> networks) {

    }
}
