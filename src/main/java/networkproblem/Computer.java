package networkproblem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Computer implements Comparable<Computer> {
    private static final String PORTS_DELIM = ";";

    private final boolean real;
    private final TreeSet<Integer> ports;
    private final HashMap<Integer, Integer> portNamesToIndex;

    public Computer(String portsString) {
        real = true;
        ports = parsePortsString(portsString);
        portNamesToIndex = createPortNamesToIndexMap();
    }

    public Computer(boolean real, int[] ports) {
        this.real = real;
        this.ports = new TreeSet<>();
        for (int portI = 0; portI < ports.length; ++portI) {
            this.ports.add(ports[portI]);
        }
        portNamesToIndex = createPortNamesToIndexMap();
    }

    private HashMap<Integer, Integer> createPortNamesToIndexMap() {
        HashMap<Integer, Integer> portNamesToIndex = new HashMap<>();
        int index = 0;
        for (Integer port : ports) {
            portNamesToIndex.put(port, index);
            ++index;
        }
        return portNamesToIndex;
    }

    public boolean isReal() {
        return real;
    }

    public TreeSet<Integer> getPorts() {
        return ports;
    }

    public String getStringRepresentation(boolean infoSet) {
        StringBuilder sb = new StringBuilder().append(infoSet ? "" : (real ? "P" : "H")).append(infoSet ? "" : ":");
        for (Integer port : ports) {
            sb.append(port).append(";");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public boolean containsPort(int i) {
        return ports.contains(i);
    }

    private TreeSet<Integer> parsePortsString(String portsString) {
        String[] portsStringSplits = portsString.split(PORTS_DELIM);
        TreeSet<Integer> portsSet = new TreeSet<>();
        for (String portsStringSplit : portsStringSplits) {
            portsSet.add(Integer.valueOf(portsStringSplit));
        }
        return portsSet;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "real=" + real +
                ", ports=" + ports +
                '}';
    }

    @Override
    public int compareTo(Computer o) {
        if (ports.size() < o.getPorts().size()) {
            return -1;
        } else if (ports.size() > o.getPorts().size()) {
            return 1;
        }
        Iterator<Integer> thisIt = ports.iterator();
        Iterator<Integer> otherIt = o.getPorts().iterator();
        int thisI, otherI;
        while (thisIt.hasNext()) {
            thisI = thisIt.next();
            otherI = otherIt.next();
            if (thisI < otherI) {
                return -1;
            } else if (thisI > otherI) {
                return 1;
            }
        }
        return 0;
    }
}
