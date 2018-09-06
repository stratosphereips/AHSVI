package networkproblem;

public class Action {

    private final ActionType actionType;
    private final String name;
    private final int targetComputerI;
    private final int targetPort;

    public Action(ActionType actionType, int targetComputerI, int targetPort) {
        this.targetComputerI = targetComputerI;
        this.targetPort = targetPort;
        this.actionType = actionType;
        this.name = actionType + "(" + targetComputerI + "," + targetPort + ")";
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getName() {
        return name;
    }

    public int getTargetComputerI() {
        return targetComputerI;
    }

    public int getTargetPort() {
        return targetPort;
    }

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name +
                '}';
    }

    public enum ActionType {
        PROBE("probe"),
        ATTACK("attack");

        private final String text;

        ActionType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
