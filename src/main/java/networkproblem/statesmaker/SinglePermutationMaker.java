package networkproblem.statesmaker;

import networkproblem.Computer;

import java.util.ArrayList;
import java.util.Collections;

public class SinglePermutationMaker extends CombinationsPermutationsMaker {

    public SinglePermutationMaker(ArrayList<Computer> network) {
        super(network);
    }

    @Override
    public void createPermutations() {
        Collections.sort(network);
        permutations.add(network);
    }

    @Override
    public int permutationsCount() {
        return 1;
    }
}
