package hsvi.bounds;

import java.util.Arrays;

public class UBPoint {
    double[] coordinates;
    double value;
    int data;
    boolean extreme = false;
    int extremeId = Integer.MIN_VALUE;

    public UBPoint(double[] coordinates, double value, int data) {
        this.coordinates = coordinates;
        this.value = value;
        this.data = data;
    }

    public UBPoint(double[] coordinates, double value) {
        this(coordinates, value, -1);
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

    public double[] getCoordinates() {
        return coordinates;
    }

    public String toString() {
        return Arrays.toString(coordinates) + ", Value = " + value;
    }
}
