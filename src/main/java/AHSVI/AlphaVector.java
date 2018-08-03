package main.java.AHSVI;

public class AlphaVector<T> {
    double[] vector;
    T data;
    boolean updated;

    public AlphaVector(double [] vector, T data) {
        this.vector = vector;
        if (data != null) {
            this.data = data;
        }
    }

    public void update(double[] vector) {
        this.vector = vector;
    }

    public void setData(T data) {
        this.data = data;
    }
}
