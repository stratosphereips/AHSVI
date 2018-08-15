package hsvi.bounds;

import helpers.HelperFunctions;
import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.*;

public class LowerBound extends Bound {

    public List<LBAlphaVector> alphaVectors;

    private double minimum = Double.POSITIVE_INFINITY;
    public double[] minimalBelief;

    public LowerBound(int dimension) {
        super(dimension);
        alphaVectors = new LinkedList<>();
    }

    public List<LBAlphaVector> getAlphaVectors() {
        return alphaVectors;
    }

    @Override
    public int size() {
        return alphaVectors.size();
    }

    @Override
    public double[] getBeliefInMinimum() {
        // TODO make this smarter
        double[] beliefInMin = null;
        try {
            IloCplex model = new IloCplex();
            model.setOut(null);
            model.setWarning(null);

            IloNumExpr obj = model.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            IloNumVar[] beliefVars = model.numVarArray(dimension, 0.0, 1.0);
            model.addEq(model.sum(beliefVars), 1.0);
            for (LBAlphaVector alphaVector : alphaVectors) {
                model.addGe(obj, model.scalProd(alphaVector.vector, beliefVars));
            }
            model.exportModel("min_belief.lp");
            model.addMinimize(obj);
            model.solve();
            return model.getValues(beliefVars);

        } catch (IloException e) {
            e.printStackTrace();
        }

        return beliefInMin;
    }

    public boolean contains(double[] vector) {
        for (LBAlphaVector alpha : alphaVectors) {
            if (Arrays.equals(alpha.vector, vector)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(LBAlphaVector alphaVector) {
        return alphaVectors.contains(alphaVector);
    }

    private int dominates(LBAlphaVector alpha1, LBAlphaVector alpha2) {
        int state = 0; // 0 no domination, -1 alpha1 dominates, 1 alpha2 dominates
        for (int i = 0; i < dimension; i++) {
            if (alpha1.vector[i] < alpha2.vector[i]) {
                if (state == 0) state = 1;
                else if (state == -1) return 0;
            } else if (alpha1.vector[i] > alpha2.vector[i]) {
                if (state == 0) state = -1;
                else if (state == 1) return 0;
            }
        }
        return state;
    }

    @Override
    public void removeDominated() {
        TreeSet<Integer> alphasToRemoveIndexes = new TreeSet<>();
        ArrayList<LBAlphaVector> alphas = new ArrayList<>(alphaVectors);
        int dominationState;
        // TODO remove dominated
        for (int i = 0; i < alphas.size(); ++i) {
            if (alphasToRemoveIndexes.contains(i)) {
                continue;
            }
            for (int j = i + 1; j < alphas.size(); ++j) {
                if (alphasToRemoveIndexes.contains(j)) {
                    continue;
                }
                dominationState = dominates(alphas.get(i), alphas.get(j));
                if (dominationState == -1) { // i dominates j
                    alphasToRemoveIndexes.add(j);
                } else if (dominationState == 1) { // j dominates i
                    alphasToRemoveIndexes.add(i);
                    break;
                }
            }
        }
        alphaVectors.clear();
        for (int i = 0; i < alphas.size(); ++i) {
            if (!alphasToRemoveIndexes.contains(i)) {
                alphaVectors.add(alphas.get(i));
            }
        }
    }

    public void addAlphaVector(LBAlphaVector alphaVector) {
        alphaVectors.add(alphaVector);
        if (1 - (double)lastPrunedSize / size() >= pruningGrowthRatio) {
            removeDominated();
            lastPrunedSize = size();
        }
    }

    public void addVector(double[] alphaVector, Integer data) {
        addAlphaVector(new LBAlphaVector(alphaVector, data));
    }

    public void printVectors() {
        for (LBAlphaVector vector : alphaVectors) {
            System.out.println(vector);
        }
    }

    @Override
    public double getValue(double[] point) {
        double max = Double.NEGATIVE_INFINITY;
        for (LBAlphaVector alphaVector : alphaVectors) {
            max = Math.max(max, HelperFunctions.dotProd(alphaVector, point));
        }
        return max;
    }

    public LBAlphaVector getDotProdArgMax(double[] belief) {
        if (belief == null) {
            return null;
        }
        LBAlphaVector maxVector = null;
        double maxDotProd = Double.NEGATIVE_INFINITY;
        double dotProd;
        for (LBAlphaVector alphaVector : alphaVectors) {
            dotProd = HelperFunctions.dotProd(alphaVector, belief);
            if (dotProd > maxDotProd) {
                maxDotProd = dotProd;
                maxVector = alphaVector;
            }
        }
        return maxVector;
    }
}
