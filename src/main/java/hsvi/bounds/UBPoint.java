package hsvi.bounds;

import hsvi.Config;

import java.util.ArrayList;
import java.util.Arrays;

public class UBPoint {
    double[] belief;
    double value;
    int data;
    boolean extreme = false;
    int extremeId = Integer.MIN_VALUE;

    private final ArrayList<Integer> nonZeroIndexes;

    public UBPoint(double[] belief, double value, int data) {
        this.belief = belief;
        this.value = value;
        this.data = data;
        nonZeroIndexes = new ArrayList<>(belief.length);
        saveNonZeroProbabilitiesIndexes(belief);
    }

    private void saveNonZeroProbabilitiesIndexes(double[] belief) {
        for (int s = 0; s < belief.length; ++s) {
            if (belief[s] > Config.ZERO) {
                nonZeroIndexes.add(s);
            }
        }
    }

    public UBPoint(double[] belief, double value) {
        this(belief, value, -1);
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public ArrayList<Integer> getNonZeroIndexes() {
        return nonZeroIndexes;
    }

    public double getValue() {
        return value;
    }

    public double[] getBelief() {
        return belief;
    }

    public String toString() {
        return Arrays.toString(belief) + ", Value = " + value;
    }
}
