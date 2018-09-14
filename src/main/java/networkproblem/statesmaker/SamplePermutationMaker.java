package networkproblem.statesmaker;

import networkproblem.Computer;

import java.util.ArrayList;
import java.util.Random;

public class SamplePermutationMaker extends AllPermutationsMaker {

    private static Random random = new Random();

    public SamplePermutationMaker(ArrayList<Computer> network) {
        super(network);
    }

    @Override
    public void createPermutations() {
        createAllPermutations();
        ArrayList<Computer> permutation = permutations.get(random.nextInt(permutations.size()));
        permutations = new ArrayList<>(1);
        permutations.add(permutation);
    }
}
