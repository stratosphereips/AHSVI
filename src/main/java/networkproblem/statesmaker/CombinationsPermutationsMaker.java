package networkproblem.statesmaker;

import networkproblem.Computer;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class CombinationsPermutationsMaker implements Iterator<ArrayList<Computer>> {
    protected final ArrayList<Computer> network;
    protected ArrayList<ArrayList<Computer>> permutations;
    private int permutationI;

    public CombinationsPermutationsMaker(ArrayList<Computer> network) {
        this.network = network;
        permutations = new ArrayList<>(permutationsCount());
        createPermutations();
        permutationI = 0;
    }

    @Override
    public boolean hasNext() {
        return permutationI < permutations.size();
    }

    @Override
    public ArrayList<Computer> next() {
        ArrayList<Computer> permutation = permutations.get(permutationI);
        ++permutationI;
        return permutation;
    }

    public abstract void createPermutations();

    public abstract int permutationsCount();
}
