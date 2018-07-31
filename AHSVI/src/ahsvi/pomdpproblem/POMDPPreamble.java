package ahsvi.pomdpproblem;

import java.util.Objects;

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
    
    public POMDPPreamble(int id) {
        this(id, null);
    }

    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final POMDPPreamble other = (POMDPPreamble) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
