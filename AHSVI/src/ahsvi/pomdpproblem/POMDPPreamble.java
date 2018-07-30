package ahsvi.pomdpproblem;

/**
 *
 * @author dansm
 */
public abstract class POMDPPreamble {
    private final int id;
    private final String name;

    public POMDPPreamble(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
}
