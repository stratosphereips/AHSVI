package hsvi.bounds;

import helpers.HelperFunctions;
import hsvi.Config;
import hsvi.CustomLogger.CustomLogger;
import hsvi.HSVIAlgorithm;
import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.*;
import java.util.logging.Logger;

public class LowerBound extends Bound {

    private static final Logger LOGGER = CustomLogger.getLogger();

    private List<LBAlphaVector> alphaVectors;

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

    public boolean contains(double[] vector) {
        for (LBAlphaVector alpha : alphaVectors) {
            if (Arrays.equals(alpha.vector, vector)) {
                return true;
            }
        }
        return false;
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

    private void removePairwiseDominated() {
        LOGGER.finest("Removing pairwise dominated - LB");
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
        LOGGER.finest("LB size before removing: " + alphas.size());
        LOGGER.finest("LB size after removing: " + alphaVectors.size());
    }

    private Map<LBAlphaVector, IloNumExpr> initExprs(IloCplex model, IloNumVar[] beliefVars) throws IloException {
        Map<LBAlphaVector, IloNumExpr> map = new HashMap<>(2 * alphaVectors.size());
        for (LBAlphaVector alpha : alphaVectors) {
            map.put(alpha, model.scalProd(alpha.vector, beliefVars));
        }
        return map;
    }

    public void removeAlphasWithNoValuesAboveOthers() {
        LOGGER.finest("Removing alphas with no values above others - LB");
        LOGGER.finest("LB size before removing: " + alphaVectors.size());
        IloCplex model;
        ListIterator<LBAlphaVector> listIt = alphaVectors.listIterator();
        try {
            model = new IloCplex();
            model.setOut(null);
            model.setWarning(null);
            IloNumVar[] beliefVars = model.numVarArray(dimension, 0.0, 1.0);
            Map<LBAlphaVector, IloNumExpr> alphaValuesMap = initExprs(model, beliefVars);
            LBAlphaVector alpha;
            IloNumExpr constrLeftSide;
            while (listIt.hasNext()) {
                alpha = listIt.next();
                constrLeftSide = model.sum(alphaValuesMap.get(alpha), -Config.CPLEX_EPSILON);
                model.addMinimize(model.constant(0));
                for (LBAlphaVector other : alphaVectors) {
                    model.addGe(constrLeftSide, alphaValuesMap.get(other));
                }
                model.addEq(model.sum(beliefVars), 1.0);
                model.solve();
                //LOGGER.finest("Model status: " + model.getStatus());
                if (model.getStatus() == IloCplex.Status.Infeasible) {
                    //LOGGER.finest("REMOVIIIIING");
                    listIt.remove();
                }
                model.clearModel();
            }
        } catch (IloException e) {
            e.printStackTrace();
            System.exit(10);
        }
        LOGGER.finest("LB size after removing: " + alphaVectors.size());
    }

    @Override
    public void removeDominated() {
        LOGGER.finest("Removing dominated - LB");
        removePairwiseDominated();
    }

    public void addAlphaVector(LBAlphaVector alphaVector) {
        alphaVectors.add(alphaVector);
        maybePrune();
    }

    public void addVector(double[] alphaVector, Integer data) {
        addAlphaVector(new LBAlphaVector(alphaVector, data));
    }

    public void printVectors() {
        for (LBAlphaVector vector : alphaVectors) {
            LOGGER.finest(vector.toString());
        }
    }

    @Override
    public double getValue(double[] belief) {
        double max = Double.NEGATIVE_INFINITY;
        for (LBAlphaVector alphaVector : alphaVectors) {
            max = Math.max(max, HelperFunctions.dotProd(alphaVector, belief));
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

    @Override
    public String toString() {
        return "LowerBound{" +
                "alphaVectors=" + alphaVectors +
                '}';
    }
}
