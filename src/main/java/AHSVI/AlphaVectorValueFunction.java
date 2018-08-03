package main.java.AHSVI;

import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlphaVectorValueFunction<T> extends ValueFunction implements Iterable<AlphaVector<T>> {
    private List<AlphaVector<T>> alphaVectors;

    private double minimum = Double.POSITIVE_INFINITY;
    public double[] minimalBelief;
    private IloCplex cplex;


    public AlphaVectorValueFunction(int dimension) {
        this(dimension, null);
    }

    public AlphaVectorValueFunction(int dimension, Object data) {
        super(dimension, data);
        alphaVectors = new LinkedList<>();
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

    public AlphaVector<T> getVector(double[] point) {
        double max = Double.NEGATIVE_INFINITY;
        AlphaVector<T> maxVector = null;
        for (AlphaVector<T> alphaVector : alphaVectors) {
            double value = 0.0;
            for (int i = 0; i < dimension; i++) value += alphaVector.vector[i] * point[i];
            if (value > max) {
                max = value;
                maxVector = alphaVector;
            }
        }
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
