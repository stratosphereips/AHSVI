package hsvi.bounds;

import java.util.List;

public class SawtoothUpperBound extends UpperBound {
    public SawtoothUpperBound(int dimension) {
        super(dimension);
    }

    public SawtoothUpperBound(int dimension, List<UBPoint> initialUBPoints) {
        this(dimension);
        initUBPoints(initialUBPoints);
    }

    @Override
    public void addPoint(UBPoint point, int a) {
        points.add(point);
        if (1 - (double)lastPrunedSize / size() >= pruningGrowthRatio) {
            removeDominated();
            lastPrunedSize = size();
        }
    }

    @Override
    public void addPoint(double[] belief, double value, int a) {
        addPoint(new UBPoint(belief, value, a));
    }

    @Override
    public double getValue(double[] point) {
        return 0;
    }

    @Override
    public void removeDominated() {
        System.out.println("Removing dominated");
    }
}
