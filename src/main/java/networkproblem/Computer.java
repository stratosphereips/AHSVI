package networkproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Computer {
    private static final String PORTS_DELIM = ";";

    private final boolean real;
    private final ArrayList<Integer> ports;
    private final HashMap<Integer, Integer> portNamesToIndex;

    public Computer(String portsString) {
        real = true;
        ports = parsePortsString(portsString);
        portNamesToIndex = createPortNamesToIndexMap();
    }

    public Computer(boolean real, int[] ports) {
        this.real = real;
        this.ports = new ArrayList<>(ports.length);
        for (int portI = 0; portI < ports.length; ++portI) {
            this.ports.add(ports[portI]);
        }
        portNamesToIndex = createPortNamesToIndexMap();
    }

    private HashMap<Integer, Integer> createPortNamesToIndexMap() {
        HashMap<Integer, Integer> portNamesToIndex = new HashMap<>();
        for (int portI = 0; portI < ports.size(); ++portI) {
            portNamesToIndex.put(ports.get(portI), portI);
        }
        return portNamesToIndex;
    }

    public boolean isReal() {
        return real;
    }

    public ArrayList<Integer> getPorts() {
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

    private ArrayList<Integer> parsePortsString(String portsString) {
        String[] portsStringSplits = portsString.split(PORTS_DELIM);
        ArrayList<Integer> portsList = new ArrayList<>(portsStringSplits.length);
        for (int portI = 0; portI < portsStringSplits.length; ++portI) {
            portsList.add(Integer.valueOf(portsStringSplits[portI]));
        }
        return portsList;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "real=" + real +
                ", ports=" + ports +
                '}';
    }
}
