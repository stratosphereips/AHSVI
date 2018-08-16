package hsvi.bounds;

import java.util.Arrays;

public class UBPoint {
    double[] belief;
    double value;
    int data;
    boolean extreme = false;
    int extremeId = Integer.MIN_VALUE;

    public UBPoint(double[] belief, double value, int data) {
        this.belief = belief;
        this.value = value;
        this.data = data;
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
