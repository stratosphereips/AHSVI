package hsvi.bounds;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class UpperBound extends Bound {

    protected List<UBPoint> points;

    public UpperBound(int dimension) {
        super(dimension);
        points = new LinkedList<>();
    }

    protected void initUBPoints(List<UBPoint> initialUBPoints) {
        if (initialUBPoints != null) {
            for (UBPoint point : initialUBPoints) {
                addPoint(point);
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

    @Override
    public double[] getBeliefInMinimum() {
        // TODO just find min among UB points?
        double[] beliefInMin = null;
        double minValue = Double.POSITIVE_INFINITY;
        for (UBPoint point : points) {
            if (point.getValue() < minValue) {
                minValue = point.getValue();
                beliefInMin = point.getCoordinates();
            }
        }
        return beliefInMin;
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
}
