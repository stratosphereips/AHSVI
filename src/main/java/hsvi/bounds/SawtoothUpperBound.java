package hsvi.bounds;

import java.util.List;

public class SawtoothUpperBound extends UpperBound {
    public SawtoothUpperBound(int dimension) {
        super(dimension);
    }

    public SawtoothUpperBound(int dimension, List<UBPoint> initialUBPoints) {
        this(dimension);

    }

    @Override
    public void addPoint(UBPoint point, int a) {

    }

    @Override
    public void addPoint(double[] belief, double value, int a) {

    }

    @Override
    public double getValue(double[] point) {
        return 0;
    }

    @Override
    public void removeDominated() {

    }
}
