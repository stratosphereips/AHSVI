package networkproblem;

public class Action {
    private final String name;
    private final String targetComputer;
    private final String targetPort;

    public Action(String name, String targetComputer, String targetPort) {
        this.name = name;
        this.targetComputer = targetComputer;
        this.targetPort = targetPort;
    }

    public String getName() {
        return name;
    }

    public String getTargetComputer() {
        return targetComputer;
    }

    public String getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", targetComputer='" + targetComputer + '\'' +
                ", targetPort='" + targetPort + '\'' +
                '}';
    }
}
