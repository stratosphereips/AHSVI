package hsvi;

public abstract class ValueFunction {
    protected static double pruningGrowthRatio = 0.1;

    protected int lastPrunedSize = 1;

    int dimension;
    Object data;

    public ValueFunction(int dimension, Object data) {
        this.dimension = dimension;
        this.data = data;
    }

    public abstract int size();

    public abstract double getValue(double[] point);

    public abstract double[] getBeliefInMinimum();

    public abstract void removeDominated();

    public Object getData() {
        return data;
    }
}
