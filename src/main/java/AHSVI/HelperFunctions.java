package main.java.AHSVI;

public class HelperFunctions {

    public static double dotProd(double[] vector1, double[] vector2) {
        double sum = 0;
        int v1Len = vector1.length;
        int v2Len = vector2.length;
        assert v1Len == v2Len : "Vectors in dot product must be of the same length.";
        for (int i = 0; i < v1Len; ++i) {
            sum += vector1[i] * vector2[i];
        }
        return sum;
    }

    public static <T> double dotProd(AlphaVector<T> alphaVector, double[] vector) {
        return dotProd(alphaVector.vector, vector);
    }

    public static <T> double dotProd(double[] vector, AlphaVector<T> alphaVector) {
        return dotProd(alphaVector.vector, vector);
    }

    public static double dotProd(double[] vector, int i, double valueI) {
        return vector[i] * valueI;
    }

    public static <T> double dotProd(AlphaVector<T> alphaVector, int i, double valueI) {
        return dotProd(alphaVector.vector, i, valueI);
    }
}
