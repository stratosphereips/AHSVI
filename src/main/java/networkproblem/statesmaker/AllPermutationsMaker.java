package networkproblem.statesmaker;

import helpers.HelperFunctions;
import networkproblem.Computer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public class AllPermutationsMaker extends CombinationsPermutationsMaker {

    public AllPermutationsMaker(ArrayList<Computer> network) {
        super(network);
    }

    @Override
    public void createPermutations() {
        createAllPermutations();
    }

    @Override
    public int permutationsCount() {
        return (int)HelperFunctions.factorial(network.size());
    }

    protected void createAllPermutations() {
        createAllPermutations(network, new TreeSet<>(), new LinkedList<>());
    }

    private void createAllPermutations(ArrayList<Computer> network, TreeSet<Integer> permutationIndexesSet, LinkedList<Integer> permutationIndexes) {
        if (network.size() == permutationIndexes.size()) {
            permutations.add(createPermutation(network, permutationIndexes));
            return;
        }
        LinkedList<Integer> permutationIndexesNew;
        for (int i = 0; i < network.size(); ++i) {
            if (!permutationIndexes.contains(i)) {
                permutationIndexesSet.add(i);
                permutationIndexesNew = new LinkedList<>(permutationIndexes);
                permutationIndexesNew.add(i);
                createAllPermutations(network, permutationIndexesSet, permutationIndexesNew);
                permutationIndexesSet.remove(i);
            }
        }
    }

    protected ArrayList<Computer> createPermutation(ArrayList<Computer> network, LinkedList<Integer> indexes) {
        ArrayList<Computer> permuatation = new ArrayList<>(network.size());
        for (Integer index : indexes) {
            permuatation.add(network.get(index));
        }
        return permuatation;
    }
}
