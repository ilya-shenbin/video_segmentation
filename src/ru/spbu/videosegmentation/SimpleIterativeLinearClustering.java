package ru.spbu.videosegmentation;


import ij.process.ImageProcessor;

import java.util.Arrays;

public class SimpleIterativeLinearClustering {
    private int segStep;
    private int width;
    private double[][] segments;
    private int maxIter;
    private double posWeight;
    private int mergingTolerance;

    public SimpleIterativeLinearClustering(int segStep, int maxIter, double posWeight, int mergingTolerance) {
        this.segStep = segStep;
        this.maxIter = maxIter;
        this.posWeight = posWeight;
        this.mergingTolerance = mergingTolerance;
    }

    public SimpleIterativeLinearClustering(int segStep) {
        this(segStep, 5, 1, 10);
    }

    public int[] fit(ImageProcessor image) {
        this.width = image.getWidth();
        double[][] data = img2features(image);
        int height = data.length / width;
        int subHeight = height / segStep;
        int subWidth = width / segStep;

        // Grid initialization
        int segI = 0;
        segments = new double[subHeight * subWidth][];
        for(int x = (width % segStep + segStep) / 2; x < width; x += segStep) {
            for(int y = (height % segStep + segStep) / 2; y < height; y += segStep) {
                segments[segI] = data[getI(x, y)].clone();
                segI++;
            }
        }

        // Labels and distances initialization
        int[] labels = new int[data.length];
        double[] distances = new double[data.length];
        for(int i = 0; i < data.length; i++) {
            labels[i] = -1;
            distances[i] = Double.POSITIVE_INFINITY;
        }
        int[] labels_count = new int[segments.length];

        // Clustering
        for(int g = 0; g < maxIter; g++) {
            // M-step
            for (int i = 0; i < segments.length; i++) {
                for (int x = Math.max(0, getSegX(i) - segStep); x < Math.min(width, getSegX(i) + segStep); x++) {
                    for (int y = Math.max(0, getSegY(i) - segStep); y < Math.min(height, getSegY(i) + segStep); y++) {
                        double dist = squaredEuclideanDistance(segments[i], data[getI(x, y)]);
                        if (dist < distances[getI(x, y)]) {
                            distances[getI(x, y)] = dist;
                            labels[getI(x, y)] = i;
                        }
                    }
                }
            }

            // E-step
            for (double[] segment : segments) {
                Arrays.fill(segment, 0);
            }
            Arrays.fill(labels_count, 0);
            for (int i = 0; i < data.length; i++) {
                vecPlusVec(segments[labels[i]], data[i]);
                labels_count[labels[i]] += 1;
            }
            for (int i = 0; i < segments.length; i++) {
                vecMultNum(segments[i], 1. / labels_count[i]);
            }
        }

        // Merging
        if(mergingTolerance > 0) {
            MSTClusterMerging merging = new MSTClusterMerging(mergingTolerance, posWeight);
            labels = merging.merge(segments, labels);
        }

        return labels;
    }

    private double[][] img2features(ImageProcessor image) {
        int size = image.getPixelCount();
        double[][] data = new double[size][];

        for(int i = 0; i < size; i++) {
            int c = image.get(i); //color
            double[] Lab = RGB2Lab.rgb2lab(int2rgb(c));
            data[i] = new double[] {
                    Lab[0]       // L
                    , Lab[1]     // a
                    , Lab[2]     // b
                    , i % width  // x
                    , i / width  // y
            };
        }
        return data;
    }

    private int getI(int x, int y) {
        return y * width + x;
    }

    private int getSegX(int i) {
        return (int)segments[i][3];
    }

    private int getSegY(int i) {
        return (int)segments[i][4];
    }

    private void vecPlusVec(double[] a, double[] b) {
        for(int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    private void vecMultNum(double[] a, double c) {
        for(int i = 0; i < a.length; i++) {
            a[i] = a[i] * c;
        }
    }

    private double[] int2rgb(int c) {
        return new double[] {
                (double)((c & 16711680) >> 16)
                , (double)((c & '\uff00') >> 8)
                , (double)(c & 255)
        };
    }

    private double squaredEuclideanDistance(double[] a, double[] b) {
        double result = 0;
        for(int i = 0; i < 3; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        for(int i = 3; i < 5; i++) {
            result += posWeight * Math.pow(a[i] - b[i], 2);
        }
        return result;
    }
}
