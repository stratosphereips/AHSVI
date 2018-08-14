package HSVI;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "AlphaVector{" +
                "vector=" + Arrays.toString(vector) +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlphaVector<?> that = (AlphaVector<?>) o;
        return Arrays.equals(vector, that.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
