package hsvi.bounds;

import hsvi.Config;
import helpers.HelperFunctions;
import hsvi.CustomLogger.CustomLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.logging.Logger;

public class SawtoothUpperBound extends UpperBound {

    private static final Logger LOGGER = CustomLogger.getLogger(SawtoothUpperBound.class.getName());

    private final double[] extremePointsValues;

    public SawtoothUpperBound(int dimension) {
        super(dimension);
        extremePointsValues = new double[dimension];
    }

    public SawtoothUpperBound(int dimension, double[] initialUBExtremePointsValues) {
        this(dimension);
        initUBPoints(initialUBExtremePointsValues);
    }

    @Override
    protected void initUBPoints(double[] initialUBExtremePointsValues) {
        HelperFunctions.copyArray(initialUBExtremePointsValues, extremePointsValues);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void addPoint(UBPoint point, int a) {
        int extremePointId = extremePointId(point.belief);
        if (extremePointId > -1) {
            extremePointsValues[extremePointId] = point.value;
            return;
        }
        points.add(point);
        maybePrune();
    }

    @Override
    public void addPoint(double[] belief, double value, int a) {
        addPoint(new UBPoint(belief, value, a));
    }

    @Override
    public double[] getBeliefInMinimum() {
        double[] beliefInMin = null;
        double minValue = Double.POSITIVE_INFINITY;
        for (UBPoint point : points) {
            if (point.getValue() < minValue) {
                minValue = point.getValue();
                beliefInMin = point.getBelief();
            }
        }
        for (int s = 1; s < dimension; ++s) {
            if (extremePointsValues[s] < minValue) {
                minValue = extremePointsValues[s];
                beliefInMin = new double[dimension];
                beliefInMin[s] = 1;
            }
        }

        return beliefInMin;
    }

    private double getValueInducedByInnerPoint(UBPoint innerPoint, double[] belief,
                                               double valueOfBeliefOnExtremePointsPlane,
                                               double valueOfInnerPointBeliefOnExtremePointsPlane) {
        if (innerPoint.getValue() >= valueOfInnerPointBeliefOnExtremePointsPlane) {
            // value of this inner point is above extreme points plane
            return Double.POSITIVE_INFINITY;
        }
        double minRatio = Double.POSITIVE_INFINITY;
        for (Integer s : innerPoint.getNonZeroIndexes()) {
            if (belief[s] < Config.ZERO) {
                // belief[s] == 0 and innerPoint.belief != 0, we know that the minRatio must be 0, so we have no new info
                return Double.POSITIVE_INFINITY;
            }
            minRatio = Math.min(minRatio, belief[s] / innerPoint.getBelief()[s]);
        }
        if (minRatio > 1) {
            minRatio = 1;
        }
        return valueOfBeliefOnExtremePointsPlane -
                minRatio * (valueOfInnerPointBeliefOnExtremePointsPlane - innerPoint.getValue());
    }

    @Override
    public double getValue(double[] belief) {
        double valueOfBeliefOnExtremePointsPlane = HelperFunctions.dotProd(extremePointsValues, belief);
        double minValue = valueOfBeliefOnExtremePointsPlane;
        double valueOfInnerPointBeliefOnExtremePointsPlane;
        for (UBPoint point : points) {
            valueOfInnerPointBeliefOnExtremePointsPlane =
                    HelperFunctions.dotProd(extremePointsValues, point.getBelief(), point.getNonZeroIndexes());
            minValue = Math.min(minValue, getValueInducedByInnerPoint(point, belief,
                    valueOfBeliefOnExtremePointsPlane, valueOfInnerPointBeliefOnExtremePointsPlane));
        }
        return minValue;
    }

    private int dominates(UBPoint p1, UBPoint p2, double[] valuesOfPointsOnExtremePointsPlane, int p1I, int p2I) {
        // 0 no domination, -1 p1 dominates, 1 p2 dominates
        double p1ValueInp2 = getValueInducedByInnerPoint(p2, p1.getBelief(),
                valuesOfPointsOnExtremePointsPlane[p1I], valuesOfPointsOnExtremePointsPlane[p2I]);
        if (p1ValueInp2 < p1.getValue()) {
            return 1;
        }
        double p2ValueInp1 = getValueInducedByInnerPoint(p1, p2.getBelief(),
                valuesOfPointsOnExtremePointsPlane[p2I], valuesOfPointsOnExtremePointsPlane[p1I]);
        if (p2ValueInp1 < p2.getValue()) {
            return -1;
        }
        return 0;
    }

    private double[] computeValuesOfPointsOnExtremePointsPlane(ArrayList<UBPoint> pointsArrayList) {
        double[] valuesOfPointsOnExtremePointsPlane = new double[pointsArrayList.size()];
        for (int i = 0; i < pointsArrayList.size(); ++i) {
            valuesOfPointsOnExtremePointsPlane[i] =
                    HelperFunctions.dotProd(extremePointsValues,
                            pointsArrayList.get(i).getBelief(), pointsArrayList.get(i).getNonZeroIndexes());
        }
        return valuesOfPointsOnExtremePointsPlane;
    }

    @Override
    public void removeDominated() {
        LOGGER.finer("Removing dominated - UB");
        TreeSet<Integer> pointsToRemoveIndexes = new TreeSet<>();
        ArrayList<UBPoint> pointsArrayList = new ArrayList<>(points);
        double[] valuesOfPointsOnExtremePointsPlane = computeValuesOfPointsOnExtremePointsPlane(pointsArrayList);
        int dominationState;
        for (int i = 0; i < pointsArrayList.size(); ++i) {
            if (pointsToRemoveIndexes.contains(i)) {
                continue;
            }
            for (int j = i + 1; j < pointsArrayList.size(); ++j) {
                if (pointsToRemoveIndexes.contains(j)) {
                    continue;
                }
                dominationState = dominates(pointsArrayList.get(i), pointsArrayList.get(j),
                        valuesOfPointsOnExtremePointsPlane, i, j);
                if (dominationState == -1) { // i dominates j
                    pointsToRemoveIndexes.add(j);
                } else if (dominationState == 1) { // j dominates i
                    pointsToRemoveIndexes.add(i);
                    break;
                }
            }
        }
        points.clear();
        for (int i = 0; i < pointsArrayList.size(); ++i) {
            if (!pointsToRemoveIndexes.contains(i)) {
                points.add(pointsArrayList.get(i));
            }
        }
        LOGGER.finer("UB size before removing: " + pointsArrayList.size());
        LOGGER.finer("UB size after removing: " + points.size());
    }

    @Override
    public String toString() {
        return "SawtoothUpperBound{" +
                "extreme points value=" + Arrays.toString(extremePointsValues) +
                ",points=" + points +
                '}';
    }
}
