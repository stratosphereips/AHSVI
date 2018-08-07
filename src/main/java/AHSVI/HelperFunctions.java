package AHSVI;

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

    public static void arrScalarProd(double[] vector, double num) {
        for (int i = 0; i < vector.length; ++i) {
            vector[i] *= num;
        }
    }

    public static void arrPairwiseProd(double[] arr1, double[] arr2) {
        assert arr1.length == arr2.length : "Vectors must have the same length to multiply";
        for (int i = 0; i < arr1.length; ++i) {
            arr1[i] *= arr2[i];
        }
    }

    public static void matrixProd(double[] arr, double[][] mat, double[] res) {
        assert arr.length == mat.length : "Vector and matrix must have the same inner dimension to multiply";
        assert arr.length == res.length : "Resulting vector must have the same length as argument vector";
        fillArray(res, 0);
        for (int j = 0; j < arr.length; ++j) {
            System.out.println("sum: " + Arrays.stream(mat[j]).sum());
            for (int p = 0; p < arr.length; ++p) {
                res[j] += arr[p] * mat[p][j];
            }
        }
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(res));
    }

    public static void arrSub(double[] arr1, double[] arr2, double[] arrRes) {
        assert arr1.length == arr2.length && arr1.length == arrRes.length : "Vectors must have the same length to subtract";
        for (int i = 0; i < arrRes.length; ++i) {
            arrRes[i] = arr1[i] - arr2[i];
        }
    }

    public static void arrSub(double[] arr1, double[] arr2) {
        assert arr1.length == arr2.length : "Vectors must have the same length to subtract";
        for (int i = 0; i < arr1.length; ++i) {
            arr1[i] -= arr2[i];
        }
    }

    public static void arrAdd(double[] arr1, double[] arr2) {
        assert arr1.length == arr2.length : "Vectors must have the same length to add";
        for (int i = 0; i < arr1.length; ++i) {
            arr1[i] += arr2[i];
        }
    }

    public static double infinityNorm(double[] arr) {
        double infN = -1;
        for (int i = 0; i < arr.length; ++i) {
            infN = Math.max(infN, Math.abs(arr[i]));
        }
        return infN;
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
}
