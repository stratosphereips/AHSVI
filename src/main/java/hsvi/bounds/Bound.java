package hsvi.bounds;

public abstract class Bound {
    private static final double PRUNING_GROWTH_RATIO = 0.1;

    protected double pruningGrowthRatio;
    protected int lastPrunedSize;

    int dimension;

    public Bound(int dimension) {
        this.dimension = dimension;
        pruningGrowthRatio = PRUNING_GROWTH_RATIO;
        lastPrunedSize = 1;
    }

    public abstract int size();

    public abstract double getValue(double[] belief);

    public abstract void removeDominated();

    public double growthRatioSinceLastPruning() {
        return 1 - (double)lastPrunedSize / size();
    }

    protected void maybePrune() {
        if (growthRatioSinceLastPruning() >= pruningGrowthRatio) {
            removeDominated();
            lastPrunedSize = size();
        }
    }
}
