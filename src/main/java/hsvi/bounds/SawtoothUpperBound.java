package hsvi.bounds;

public class SawtoothUpperBound extends UpperBound {
    public SawtoothUpperBound(int dimension) {
        super(dimension);
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
