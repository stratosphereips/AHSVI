package hsvi.bounds;

import hsvi.Config;

import java.util.LinkedList;
import java.util.List;

public abstract class UpperBound extends Bound {

    protected List<UBPoint> points;

    public UpperBound(int dimension) {
        super(dimension);
        points = new LinkedList<>();
    }

    protected void initUBPoints(double[] initialUBExtremePointsValues) {
        double[] extremeBelief;
        if (initialUBExtremePointsValues != null) {
            for (int s = 0; s < dimension; ++s) {
                extremeBelief = new double[dimension];
                extremeBelief[s] = 1;
                addPoint(extremeBelief, initialUBExtremePointsValues[s]);
            }
        }
    }

    public List<UBPoint> getPoints() {
        return points;
    }

    @Override
    public int size() {
        return points.size();
    }

    public abstract void addPoint(double[] belief, double value, int a);

    public abstract void addPoint(UBPoint point, int a);

    public void addPoint(UBPoint point) {
        addPoint(point, -1);
    }

    public void addPoint(double[] belief, double value) {
        addPoint(belief, value, -1);
    }

    public void addPoint(double[] belief) {
        addPoint(belief, getValue(belief));
    }

    protected static int extremePointId(double[] belief) {
        for (int i = 0; i < belief.length; i++) {
            if (belief[i] >= 1 - Config.ZERO) return i;
        }
        return -1;
    }
}
