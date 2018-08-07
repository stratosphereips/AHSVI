package AHSVI;

public class AlphaVector<T> {
    public double[] vector;
    public T data;
    public boolean updated;

    public AlphaVector(double[] vector, T data) {
        this.vector = vector;
        this.data = data;
    }

    public void update(double[] vector) {
        this.vector = vector;
    }

    public void setData(T data) {
        this.data = data;
    }
}
