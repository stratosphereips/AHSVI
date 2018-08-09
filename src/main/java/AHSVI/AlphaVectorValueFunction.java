package AHSVI;

import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.*;

public class AlphaVectorValueFunction<T> extends ValueFunction implements Iterable<AlphaVector<T>> {
    // TODO RANDOOOOOOOOOOM
    Random rand;

    public List<AlphaVector<T>> alphaVectors;

    private double minimum = Double.POSITIVE_INFINITY;
    public double[] minimalBelief;


    public AlphaVectorValueFunction(int dimension) {
        this(dimension, null);
    }

    public AlphaVectorValueFunction(int dimension, Object data) {
        super(dimension, data);
        rand = new Random(System.currentTimeMillis());
        alphaVectors = new LinkedList<>();
    }

    public boolean contains(double[] vector) {
        for (AlphaVector<T> alpha : alphaVectors) {
            if (Arrays.equals(alpha.vector, vector)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(AlphaVector<T> alphaVector) {
        return alphaVectors.contains(alphaVector);
    }

    @Override
    public void removeDominated() {
        LinkedList<AlphaVector<T>> alphasToRemove = new LinkedList<>();
        double[] belief = new double[alphaVectors.get(0).vector.length];
        // TODO remove dominated
    }

    public AlphaVector<T> addVector(double[] alphaVector) {
        return addVector(alphaVector, null);
    }

    public AlphaVector<T> addVector(double[] alphaVector, Integer data) {
        AlphaVector<T> vector = new AlphaVector(alphaVector, data);
        alphaVectors.add(vector);

        for (int i = 0; i < alphaVector.length; i++) {
            minimum = Math.min(minimum, alphaVector[i]);
        }

        return vector;
    }

    public void printVectors() {
        for (AlphaVector<T> vector : alphaVectors) {
            System.out.println(vector);
        }
    }

    @Override
    public double getValue(double[] point) {
        double max = Double.NEGATIVE_INFINITY;
        for (AlphaVector<T> alphaVector : alphaVectors) {
            double value = 0.0;
            for (int i = 0; i < dimension; i++) value += alphaVector.vector[i] * point[i];
            max = Math.max(value, max);
        }
        return max;
    }

    public AlphaVector<T> getDotProdArgMax(double[] belief) {
        //System.out.println("Computing argmax alphaVec for belief: " + Arrays.toString(belief));
        if (belief == null) {
            return null;
        }
        LinkedList<AlphaVector<T>> bestAlphas = new LinkedList<>();
        AlphaVector<T> maxVector = null;
        double maxDotProd = Double.NEGATIVE_INFINITY;
        double dotProd;
        for (AlphaVector<T> alphaVector : alphaVectors) {
            dotProd = HelperFunctions.dotProd(alphaVector, belief);
            //System.out.println("\t\tVector: " + alphaVector);
            //System.out.println("\t\tValue: " + dotProd);
            if (dotProd > maxDotProd) {
                maxDotProd = dotProd;
                maxVector =alphaVector;
            }
//            if (dotProd > maxDotProd) {
//                maxDotProd = dotProd;
//                bestAlphas.clear();
//                bestAlphas.add(alphaVector);
//            } else if (dotProd - maxDotProd < Config.ZERO) {
//                bestAlphas.add(alphaVector);
//            }
        }
        //System.out.println("\tArgmax: " + maxVector); // TODO  print
//        if (!bestAlphas.isEmpty()) {
//            maxVector = bestAlphas.get(rand.nextInt(bestAlphas.size()));
//        }
        return maxVector;
    }

    public int numVectors() {
        return alphaVectors.size();
    }


    private static boolean dominates(AlphaVector alpha1, AlphaVector alpha2) {
        for (int i = 0; i < alpha1.vector.length; i++) {
            if (alpha1.vector[i] < alpha2.vector[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<AlphaVector<T>> iterator() {
        return alphaVectors.iterator();
    }

    private IloLPMatrix matrix;

    @Override
    public IloRange constructLP(IloCplex cplex, IloNumVar[] coords, IloNumVar value) throws IloException {
        IloNumVar[] vars = new IloNumVar[coords.length + 1];
        for (int i = 0; i < coords.length; i++) vars[i] = coords[i];
        vars[coords.length] = value;

        int V = numVectors();
        double[] lpLb = new double[V];
        double[] lpUb = new double[V];
        int[][] lpInd = new int[V][dimension + 1];
        double[][] lpCoef = new double[V][dimension + 1];

        int i = 0;
        for (AlphaVector<T> vector : alphaVectors) {
            lpUb[i] = Double.POSITIVE_INFINITY;
            for (int j = 0; j < dimension; j++) {
                lpInd[i][j] = j;
                lpCoef[i][j] = -vector.vector[j];
            }
            lpInd[i][coords.length] = coords.length;
            lpCoef[i][coords.length] = 1.0;
            i++;
        }

        matrix = cplex.addLPMatrix();
        matrix.addCols(vars);
        matrix.addRows(lpLb, lpUb, lpInd, lpCoef);

        return null;
    }

    public IloRange constructLPConditional(IloCplex cplex, IloNumVar[] coords, IloNumVar value) throws IloException {
        IloNumVar[] vars = new IloNumVar[coords.length + 1];
        for (int i = 0; i < coords.length; i++) vars[i] = coords[i];
        vars[coords.length] = value;

        int V = numVectors();
        double[] lpLb = new double[V];
        double[] lpUb = new double[V];
        int[][] lpInd = new int[V][dimension + 1];
        double[][] lpCoef = new double[V][dimension + 1];

        int i = 0;
        for (AlphaVector<T> vector : alphaVectors) {
            lpUb[i] = Double.POSITIVE_INFINITY;
            for (int j = 0; j < dimension; j++) {
                lpInd[i][j] = j;
                lpCoef[i][j] = -vector.vector[j];
            }
            lpInd[i][coords.length] = coords.length;
            lpCoef[i][coords.length] = 1.0;
            i++;
        }

        matrix = cplex.addLPMatrix();
        matrix.addCols(vars);
        matrix.addRows(lpLb, lpUb, lpInd, lpCoef);

        return null;
    }

    public int auxiliaryRanges(IloRange[] dest, int offset) throws IloException {
        IloRange[] ranges = matrix.getRanges();
        for (int i = 0; i < ranges.length; i++) {
            dest[offset + i] = ranges[i];
        }
        return offset + ranges.length;
    }

    public List<AlphaVector<T>> getVectors() {
        return alphaVectors;
    }

    public double getMinimum() {
        return minimum;
    }

    public void updateMinimum() throws IloException {
        IloCplex cplex = Cplex.get();
        cplex.clearModel();
        IloNumVar[] coords = cplex.numVarArray(dimension, 0.0, 1.0);
        IloNumVar value = cplex.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        cplex.addEq(cplex.sum(coords), 1.0);
        constructLP(cplex, coords, value);
        cplex.addMinimize(value);
        cplex.solve();
        cplex.exportModel("cplex.lp");
        minimum = cplex.getObjValue();

        //extract result
        this.minimalBelief = new double[dimension];
        for (int i = 0; i < coords.length; i++) {
            minimalBelief[i] = cplex.getValue(coords[i]);
        }
    }
}
