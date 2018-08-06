package main.java.AHSVI;

import java.util.Arrays;

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

    public static void fillArray(double[] arr, double num) {
        Arrays.fill(arr, num);
    }

    public static void fillArray(int[] arr, int num) {
        Arrays.fill(arr, num);
    }

    public static void copyArray(double[] arrSource, double[] arrDestination) {
        System.arraycopy(arrSource, 0, arrDestination, 0, arrDestination.length);
    }

    public static double infinityNorm(double[] arr) {
        int infN = -1;
        for (int i = 0; i < arr.length; ++i) {
            infN = Math.max(infN, (int)Math.abs(arr[i]));
        }
        return infN;
    }

    public static void arrSub(double[] arr1, double[] arr2, double[] arrRes) {
        assert arr1.length == arr2.length && arr1.length == arrRes.length: "Vectors must have the same length to subtract";
        for (int i = 0; i < arrRes.length; ++i) {
            arrRes[i] = arr1[i] - arr2[i];
        }
    }

    public static void arrSub(double[] arr1, double[] arr2) {
        assert arr1.length == arr2.length: "Vectors must have the same length to subtract";
        for (int i = 0; i < arr1.length; ++i) {
            arr1[i] = arr1[i] - arr2[i];
        }
    }

    public static void arrAdd(double[] arr1, double[] arr2) {
        assert arr1.length == arr2.length: "Vectors must have the same length to subtract";
        for (int i = 0; i < arr1.length; ++i) {
            arr1[i] = arr1[i] + arr2[i];
        }
    }
}
