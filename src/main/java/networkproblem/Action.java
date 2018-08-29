package networkproblem;

public class Action {

    private final ActionType actionType;
    private final String name;
    private final int targetComputerI;
    private final int targetPortI;

    public Action(ActionType actionType, int targetComputerI, int targetPortI) {
        this.targetComputerI = targetComputerI;
        this.targetPortI = targetPortI;
        this.actionType = actionType;
        this.name = actionType + "(" + targetComputerI + "," + targetPortI + ")";
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

    public int getTargetPortI() {
        return targetPortI;
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
