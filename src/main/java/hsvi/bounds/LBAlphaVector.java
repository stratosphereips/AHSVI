package hsvi.bounds;

import java.util.Arrays;
import org.apache.commons.lang.math.NumberUtils;

public class LBAlphaVector{

    public final double[] vector;
    private final double maxValue;
    public final int a;


    public LBAlphaVector(double[] vector, int a) {
        this.vector = vector;
        maxValue = NumberUtils.max(vector);
        this.a = a;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public int getAction() {
        return a;
    }

    @Override
    public String toString() {
        return "LBAlphaVector{" +
                "vector=" + Arrays.toString(vector) +
                ", maxValue=" + maxValue +
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
