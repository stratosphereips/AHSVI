package hsvi.bounds;

import ahsvi.Config;
import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by wigos on 16.5.16.
 */
public class CplexLPUpperBound extends UpperBound {
    private IloCplex cplex;
    public static double RANDOMIZE = Double.NaN;
    public static boolean CACHED_CPLEX = true;

    private int nPoints = 0;
    private IloNumVar[] alphas;

    private UBPoint[] extremePoints;

    private double maximum = Double.NEGATIVE_INFINITY;

    private IloCplex cachedCplex = null;
    private IloNumVar cachedValueVar = null;
    private IloLPMatrix cachedLPMatrix = null;
    private IloRange[] cachedLPRanges = null;
    public double minimum;
    public UBPoint minimalBelief;

    public CplexLPUpperBound(int dimension) {
        this(dimension, null);
    }

    public CplexLPUpperBound(int dimension, List<UBPoint> initialUBPoints) {
        super(dimension);
        try {
            cplex = new IloCplex();
            cplex.setParam(IloCplex.IntParam.RootAlg, 2);
        } catch (IloException e) {
            e.printStackTrace();
            System.exit(10);
        }
        extremePoints = new UBPoint[dimension];
        initUBPoints(initialUBPoints);
    }

    @Override
    public void removeDominated() {
        List<UBPoint> newPoints = new LinkedList<>();
        Iterator<UBPoint> it = points.iterator();
        int removed = 0;
        while (it.hasNext()) {
            UBPoint current = it.next();
            if (current.value - getValue(current.coordinates) > Config.ZERO) {
                removed++;
            } else {
                newPoints.add(current);
            }
        }
        nPoints -= removed;
        points = newPoints;

        if (cachedCplex != null) {
            try {
                cachedCplex.clearModel();
                rebuildModel(new double[dimension]);
            } catch (IloException iloe) {
                iloe.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public void addPoint(UBPoint point, int a) {
        addPoint(point.coordinates, point.value);
    }

    @Override
    public void addPoint(double[] point, double value, int data) {
        int extremeId = extremeId(point);
        if (extremeId >= 0) {
            UBPoint extremePoint = (UBPoint) extremePoints[extremeId];
            if (extremePoint == null) {
                extremePoint = new UBPoint(point, Double.POSITIVE_INFINITY, data);
                extremePoints[extremeId] = extremePoint;
                extremePoint.extreme = true;
                extremePoint.extremeId = extremeId;
                points.add(extremePoint);
                nPoints++;

                if (cachedCplex != null) {
                    try {
                        IloNumVar var = cachedCplex.numVar(0.0, 1.0);
                        int[] ind = new int[point.length + 1];
                        double[] val = new double[point.length + 1];

                        for (int i = 0; i < point.length; i++) {
                            ind[i] = i;
                            val[i] = point[i];
                        }
                        ind[point.length] = point.length;
                        val[point.length] = -value;

                        cachedLPMatrix.addColumn(var, ind, val);
                    } catch (IloException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
//            extremePoint.coordinates = point;
            if (value < extremePoint.value) {
                extremePoint.value = value;
                extremePoint.data = data;

                maximum = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < extremePoints.length; i++) {
                    if (extremePoints[i] == null) continue;
                    maximum = Math.max(maximum, extremePoints[i].value);
                }

                if (cachedCplex != null) {
                    try {
                        int colIdx = points.indexOf(extremePoint);

                        int[] rowind = new int[point.length + 1];
                        int[] colind = new int[point.length + 1];
                        double[] val = new double[point.length + 1];

                        for (int i = 0; i < point.length; i++) {
                            rowind[i] = i;
                            colind[i] = colIdx;
                            val[i] = point[i];
                        }
                        rowind[point.length] = point.length;
                        colind[point.length] = colIdx;
                        val[point.length] = -value;

                        cachedLPMatrix.setNZs(rowind, colind, val);
                    } catch (IloException iloe) {
                        iloe.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        } else {
            UBPoint pointObj = new UBPoint(point, value, data);
            points.add(pointObj);
            nPoints++;

            if (cachedCplex != null) {
                try {
                    IloNumVar var = cachedCplex.numVar(0.0, 1.0);
                    int[] ind = new int[point.length + 1];
                    double[] val = new double[point.length + 1];

                    for (int i = 0; i < point.length; i++) {
                        ind[i] = i;
                        val[i] = point[i];
                    }
                    ind[point.length] = point.length;
                    val[point.length] = -value;

                    cachedLPMatrix.addColumn(var, ind, val);
                } catch (IloException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public double getMinimum() {

        this.minimum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < extremePoints.length; i++) {
            if (extremePoints[i] == null) continue;
            if (minimum > extremePoints[i].value) {
                minimalBelief = extremePoints[i];
                minimum = extremePoints[i].value;
            }
        }
        return minimum;
    }

    public void randomDelete() {
        int count = points.size();
        double prob = 10.0 / count;
        Iterator<UBPoint> it = points.iterator();
        while (it.hasNext()) {
           UBPoint point = it.next();
            if (Math.random() < prob) {
                if (getValue(point.coordinates) < point.value) it.remove();
            }
        }
    }

    public int numPoints() {
        return points.size();
//        return nPoints;
    }

    public IloNumVar[] getVars() {
        return alphas;
    }

    @Override
    public double getValue(double[] point) {

        if (CACHED_CPLEX) return getValueFast(point);

        /*
        try {
            IloCplex cplex = Cplex.get();
            cplex.clearModel();

            IloNumVar value = cplex.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            constructLP(cplex, point, value);
            cplex.addMinimize(value);

            cplex.goMilp();
            double numValue = cplex.getValue(value);

            return numValue;
        } catch(IloException iloe) {
            iloe.printStackTrace();
        }
        */

        return Double.NaN;
    }

//    public double findMinimumFP() {
//        try {
//            Timing.time("UB-project");
//
//            IloCplex cplex = new IloCplex();
//            cplex.setOut(null);
//            cplex.setWarning(null);
//            IloNumVar cachedValueVar2 = cachedCplex.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
//
//            IloLPMatrix matrix = cplex.addLPMatrix();
//            alphas = cplex.numVarArray(nPoints, 0, 1);
//
//            matrix.addCols(alphas);
//            matrix.addColumn(value);
//
//            double[] lb = new double[dimension+1];
//            double[] ub = new double[dimension+1];
//            int[][] ind = new int[dimension+1][nPoints+1];
//            double[][] val = new double[dimension+1][nPoints+1];
//
//            buildMatrix(ind, val);
//
//            for(int i = 0 ; i < dimension ; i++) {
//                lb[i] = ub[i] = coords[i];
//            }
//            ub[dimension] = Double.POSITIVE_INFINITY;
//            ind[dimension][nPoints] = nPoints;
//            val[dimension][nPoints] = 1.0;
//
//            matrix.addRows(lb, ub, ind, val);
//
//
//            IloRange[] cachedLPRanges2 = matrix.getRanges();
//
//            cachedCplex.addMinimize(cachedValueVar2);
//
//            cachedCplex.goMilp();
//            double value = cachedCplex.getObjValue();
//
//            Timing.timed();
//
//            return value;
//
//        } catch(IloException iloe) {
//            iloe.printStackTrace();
//        }
//
//        return Double.NaN;
//    }

    public double getValueST(double[] point) {

        // not implemented yet
        for (UBPoint point2 : this.points) {
            double value = evaluateSTAtPoint(point, point2);
        }

        return 0d;

    }

    private double evaluateSTAtPoint(double[] point, UBPoint point2) {

        double[] vector = getDirectionalVector(point, point2);
        double minK = 1d;
        for (int i = 0; i < vector.length; i++) {
            double k = 0;
            if (vector[i] > 0) {
                k = point2.coordinates[i] / vector[i];
            }
            if (vector[i] < 0) {
                k = -point2.coordinates[i] / vector[i];
            }

            if (k > 0 && k < minK) {
                minK = k;
            }
        }

        // compute facet coordinates


        return 0;

    }

    private double[] getDirectionalVector(double[] point, UBPoint point2) {
        return IntStream.range(0, point.length)
                .mapToDouble(i -> point2.coordinates[i] - point[i])
                .toArray();
    }

    public double getValueFast(double[] point) {
        try {
            if (cachedCplex == null) {
                cachedCplex = new IloCplex();
                cachedCplex.setOut(null);
                cachedCplex.setWarning(null);
                cachedCplex.setParam(IloCplex.BooleanParam.PreInd, false);

                rebuildModel(point);
            } else {
                for (int i = 0; i < point.length; i++) {
                    cachedLPRanges[i].setBounds(point[i], point[i]);
                }
            }

            //cachedCplex.exportModel("getValueFast.lp");
            cachedCplex.solve();

            double value = cachedCplex.getObjValue();

            return value;
        } catch (IloException iloe) {
            iloe.printStackTrace();
        }

        return Double.NaN;
    }

    private void rebuildModel(double[] point) throws IloException {
        cachedValueVar = cachedCplex.numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        cachedLPMatrix = constructLP(cachedCplex, point, cachedValueVar);
        cachedLPRanges = cachedLPMatrix.getRanges();

        cachedCplex.addMinimize(cachedValueVar);
    }

    public IloRange constructLPSlow(IloNumExpr[] coords, IloNumVar value) throws IloException {
        IloNumVar[] alphas = cplex.numVarArray(nPoints, 0, 1);
        IloNumExpr[] coordSum = new IloNumExpr[dimension];
        for (int i = 0; i < dimension; i++) coordSum[i] = cplex.numExpr();
        IloNumExpr valueExpr = cplex.numExpr();

        Iterator<UBPoint> it = points.iterator();
        for (int i = 0; it.hasNext(); i++) {
            UBPoint current = it.next();
            for (int j = 0; j < dimension; j++) {
                coordSum[j] = cplex.sum(coordSum[j], cplex.prod(current.coordinates[j], alphas[i]));
            }
            valueExpr = cplex.sum(valueExpr, cplex.prod(current.value, alphas[i]));
        }

        for (int i = 0; i < dimension; i++) {
            cplex.addEq(coords[i], coordSum[i]);
        }
        return cplex.addGe(cplex.diff(value, valueExpr), 0.0);
    }

    public IloRange constructLP(IloCplex cplex, IloNumVar[] coords, IloNumVar value) throws IloException {
        IloLPMatrix matrix = cplex.addLPMatrix();
        alphas = cplex.numVarArray(nPoints, 0, 1);

        matrix.addCols(alphas);
        matrix.addCols(coords);
        matrix.addColumn(value);

        double[] lb = new double[dimension + 1];
        double[] ub = new double[dimension + 1];
        int[][] ind = new int[dimension + 1][nPoints + 1];
        double[][] val = new double[dimension + 1][nPoints + 1];

        buildMatrix(ind, val);

        for (int i = 0; i < dimension; i++) {
            ind[i][nPoints] = nPoints + i;
            val[i][nPoints] = -1.0;
        }
        ub[dimension] = Double.POSITIVE_INFINITY;
        ind[dimension][nPoints] = nPoints + dimension;
        val[dimension][nPoints] = 1.0;

        matrix.addRows(lb, ub, ind, val);

        return matrix.getRange(dimension);
    }

    public IloLPMatrix constructLP(IloCplex cplex, double[] coords, IloNumVar value) throws IloException {
        IloLPMatrix matrix = cplex.addLPMatrix();
        alphas = cplex.numVarArray(nPoints, 0, 1);

        matrix.addCols(alphas);
        matrix.addColumn(value);

        double[] lb = new double[dimension + 1];
        double[] ub = new double[dimension + 1];
        int[][] ind = new int[dimension + 1][nPoints + 1];
        double[][] val = new double[dimension + 1][nPoints + 1];

        buildMatrix(ind, val);

        for (int i = 0; i < dimension; i++) {
            lb[i] = ub[i] = coords[i];
        }
        ub[dimension] = Double.POSITIVE_INFINITY;
        ind[dimension][nPoints] = nPoints;
        val[dimension][nPoints] = 1.0;

        matrix.addRows(lb, ub, ind, val);

        return matrix;
    }

    private void buildMatrix(int[][] ind, double[][] val) {
        Iterator<UBPoint> it = points.iterator();
        for (int i = 0; it.hasNext(); i++) {
            UBPoint current = it.next();
            for (int j = 0; j < dimension; j++) {
                ind[j][i] = i;
                val[j][i] = current.coordinates[j];
            }
            ind[dimension][i] = i;
            if (Double.isNaN(RANDOMIZE)) val[dimension][i] = -current.value;
            else val[dimension][i] = -current.value + Math.random() * RANDOMIZE;
        }
    }

    public IloRange constructLP(IloCplex cplex, IloNumVar[] coordVars, List<List<Integer>> coordInd, List<List<Double>> coordVal,
                                IloNumVar value, List<Integer> spareInd, List<Double> spareVal) throws IloException {
        IloLPMatrix matrix = cplex.addLPMatrix();
        alphas = cplex.numVarArray(nPoints, 0, 1);

        addCols(matrix, alphas, new IloNumVar[]{value}, coordVars);

        double[] lb = new double[dimension + 1];
        double[] ub = new double[dimension + 1];
        int[][] ind = new int[dimension + 1][];
        double[][] val = new double[dimension + 1][];

        for (int i = 0; i < dimension; i++) {
            ind[i] = new int[coordInd.get(i).size() + nPoints];
            val[i] = new double[coordInd.get(i).size() + nPoints];
        }
        ind[dimension] = new int[nPoints + spareInd.size() + 1];
        val[dimension] = new double[nPoints + spareInd.size() + 1];

        buildMatrix(ind, val);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < coordInd.get(i).size(); j++) {
                ind[i][nPoints + j] = nPoints + coordInd.get(i).get(j) + 1;
                val[i][nPoints + j] = -coordVal.get(i).get(j);
            }
        }
        for (int j = 0; j < spareInd.size(); j++) {
            ind[dimension][nPoints + j] = nPoints + spareInd.get(j) + 1;
            val[dimension][nPoints + j] = -spareVal.get(j);
        }
        ub[dimension] = Double.POSITIVE_INFINITY;
        ind[dimension][nPoints + spareInd.size()] = nPoints;
        val[dimension][nPoints + spareInd.size()] = 1.0;

        matrix.addRows(lb, ub, ind, val);

        return matrix.getRange(dimension);
    }

    private static int extremeId(double[] belief) {
        for (int i = 0; i < belief.length; i++) {
            if (belief[i] >= 1 - Config.ZERO) return i;
        }
        return -1;
    }

    private static int addCols(IloLPMatrix matrix, IloNumVar[]... vars) throws IloException {
        int totalVars = 0;
        for(int i = 0 ; i < vars.length ; i++) totalVars += vars[i].length;

        IloNumVar[] cols = new IloNumVar[totalVars];
        int offset = 0;
        for(int i = 0 ; i < vars.length ; i++) {
            for(int j = 0 ; j < vars[i].length ; j++) {
                cols[offset++] = vars[i][j];
            }
        }

        return matrix.addCols(cols);
    }

    public double[] getAlphas() throws IloException {
        return cplex.getValues(alphas);
    }


    @Override
    public String toString() {
        return "PointBasedValueFunction{" +
                "points=" + points +
                '}';
    }
}
