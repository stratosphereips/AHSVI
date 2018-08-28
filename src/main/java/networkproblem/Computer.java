package networkproblem;

import java.util.ArrayList;
import java.util.Arrays;

public class Computer {
    private static final String PORTS_DELIM = ";";

    private final boolean real;
    private final ArrayList<String> ports;

    public Computer(String portsString) {
        real = true;
        ports = parsePortsString(portsString);
    }

    public Computer(boolean real, String[] ports) {
        this.real = real;
        this.ports = new ArrayList<>(Arrays.asList(ports));
    }

    public Computer(String[] ports) {
        this(false, ports);
    }

    public Computer(Computer oldComputer) {
        real = oldComputer.real;
        ports = new ArrayList<>(oldComputer.ports);
    }

    public boolean isReal() {
        return real;
    }

    public ArrayList<String> getPorts() {
        return ports;
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
