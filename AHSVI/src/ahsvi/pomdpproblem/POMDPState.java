package ahsvi.pomdpproblem;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dansm
 */
public class POMDPState extends POMDPPreamble {
    
    private final LinkedList<POMDPAction> actions;

    public POMDPState(int id, String name) {
        super(id, name);
        actions = new LinkedList<>();
    }

    public POMDPState(int id) {
        this(id, null);
    }

    public List<POMDPAction> getActions() {
        return actions;
    }
    
    public void addAction(POMDPAction action) {
        actions.add(action);
    }
}
