package networkproblem.statesmaker;

import networkproblem.Computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SamplePermutationMaker extends AllPermutationsMaker {

    private static Random random = new Random();

    public SamplePermutationMaker(ArrayList<Computer> network) {
        super(network);
    }

    @Override
    public void createPermutations() {
        ArrayList<Computer> sampledPermutation = new ArrayList<>(network);
        Collections.shuffle(sampledPermutation, random);
        permutations = new ArrayList<>(1);
        permutations.add(sampledPermutation);
    }
}
