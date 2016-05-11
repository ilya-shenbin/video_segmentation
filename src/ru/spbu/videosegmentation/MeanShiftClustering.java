package ru.spbu.videosegmentation;


public class MeanShiftClustering {
    private int dim;
    private final int max_iter;
    private final int n_count;

    public MeanShiftClustering(int max_iter, int n_count) {
        this.max_iter = max_iter;
        this.n_count = n_count;
    }

    public MeanShiftClustering() {
        this(3, 5);
    }

    public double[][] fit(double[][] data) {
        int size = data.length;
        if(size == 0) {
            return null;
        }

        dim = data[0].length;

        double[][] result = new double[size][];
        for(int i = 0; i < size; i++) {
            result[i] = data[i].clone();
        }

        for(int i = 0; i < size; i++) {
            for(int g = 0; g < max_iter; g++) {
                double weight;
                double weight_sum = 0;
                double[] sum = new double[dim];
                for(int j = 0; j < size; j++) {
                    weight = kernel(data[j], data[i], new double[]{20, 10});
                    if(weight == 1) {
                        vecPlusVec(sum, data[j]);
                    }
                    weight_sum += weight;
                }
                vecMultNum(sum, 1. / weight_sum);
                result[i] = sum;
            }
        }

        return result;
    }

    private void vecPlusVec(double[] a, double[] b) {
        for(int i = 0; i < dim; i++) {
            a[i] += b[i];
        }
    }

    private void vecMultNum(double[] a, double c) {
        for(int i = 0; i < dim; i++) {
            a[i] = a[i] * c;
        }
    }

    private double kernel(double[] a, double[] b, double[] params) {
        boolean isRBG = Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2) + Math.pow(a[2] - b[2], 2) < Math.pow(params[0], 2);
        boolean isXY = Math.pow(a[3] - b[3], 2) + Math.pow(a[4] - b[4], 2) < Math.pow(params[1], 2);
        return isRBG && isXY ? 1 : 0;
    }

    private double[] extendVector(double[] a) {
        double[] result = new double[a.length + 1];
        System.arraycopy(a, 0, result, 0, a.length);
        return result;
    }

    private double squaredEuclideanDistance(double[] a, double[] b) {
        double result = 0;
        for(int i = 0; i < a.length; i++) {
            result += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return result;
    }

    private void print(double[] a) {
        for(int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }
}
