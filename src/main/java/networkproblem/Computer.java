package networkproblem;

import java.util.ArrayList;
import java.util.Arrays;

public class Computer {
    private static final String PORTS_DELIM = ";";

    private final ArrayList<String> openPorts;

    public Computer(String portsString) {
        openPorts = parsePortsString(portsString);
    }

    public ArrayList<String> getOpenPorts() {
        return openPorts;
    }

    private ArrayList<String> parsePortsString(String portsString) {
        String[] ports = portsString.split(PORTS_DELIM);
        return new ArrayList<>(Arrays.asList(ports));
    }

    @Override
    public String toString() {
        return "Computer{" +
                "openPorts=" + openPorts +
                '}';
    }
}
