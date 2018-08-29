package networkproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Computer {
    private static final String PORTS_DELIM = ";";

    private final boolean real;
    private final ArrayList<String> ports;
    private final HashMap<String, Integer> portNamesToIndex;

    public Computer(String portsString) {
        real = true;
        ports = parsePortsString(portsString);
        portNamesToIndex = createPortNamesToIndexMap();
    }

    public Computer(boolean real, String[] ports) {
        this.real = real;
        this.ports = new ArrayList<>(Arrays.asList(ports));
        portNamesToIndex = createPortNamesToIndexMap();
    }

    private HashMap<String, Integer> createPortNamesToIndexMap() {
        HashMap<String, Integer> portNamesToIndex = new HashMap<>();
        for (int portI = 0; portI < ports.size(); ++portI) {
            portNamesToIndex.put(ports.get(portI), portI);
        }
        return portNamesToIndex;
    }

    public boolean isReal() {
        return real;
    }

    public ArrayList<String> getPorts() {
        return ports;
    }

    private String createStringRepresentation() {
        StringBuilder sb = new StringBuilder(real ? "P" : "H").append("{");
        for (int portI = 0; portI < ports.size(); ++portI) {
            sb.append( ports.get(portI)).append(portI < ports.size() - 1 ? "," : "");
        }
        return sb.append("}").toString();
    }

    public String getStringRepresentation() {
        return createStringRepresentation();
    }

    public boolean containsPortAtIndex(int i) {
        return i < ports.size();
    }

    private ArrayList<String> parsePortsString(String portsString) {
        String[] ports = portsString.split(PORTS_DELIM);
        return new ArrayList<>(Arrays.asList(ports));
    }

    @Override
    public String toString() {
        return "Computer{" +
                "real=" + real +
                ", ports=" + ports +
                '}';
    }
}
