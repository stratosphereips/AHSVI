package ahsvi.pomdpproblem;

/**
 *
 * @author dansm
 */
public class POMDPObservation extends POMDPPreambleWithProbDist{

    public POMDPObservation(int id, String name) {
        super(id, name);
    }

    public POMDPObservation(int id) {
        this(id, null);
    }

}
