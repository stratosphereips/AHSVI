package hsvi.bounds;

import java.util.Arrays;

public class LBAlphaVector {

    public double[] vector;
    public int a;


    public LBAlphaVector(double[] vector, int a) {
        this.vector = vector;
        this.a = a;
    }

    public void update(double[] vector) {
        this.vector = vector;
    }

    public void setAction(int a) {
        this.a = a;
    }

    public int getAction() {
        return a;
    }

    @Override
    public String toString() {
        return "AlphaVector{" +
                "vector=" + Arrays.toString(vector) +
                ", a=" + a +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LBAlphaVector that = (LBAlphaVector) o;
        return Arrays.equals(vector, that.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
