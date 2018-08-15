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

    public void addPoint(double[] belief, double value) {
        addPoint(belief, value, -1);
    }

    public void addPoint(double[] belief) {
        addPoint(belief, getValue(belief));
    }
}
