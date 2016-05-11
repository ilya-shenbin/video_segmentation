package ru.spbu.videosegmentation;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import junit.framework.Assert;
import org.junit.Test;

import java.awt.*;


public class SimpleIterativeLinearClusteringTest extends Assert {
    private int width;

    @Test
    public void testSimple() {
        ImagePlus imgPlus = new ImagePlus("data/silc_test.png");
        ImageProcessor image = imgPlus.getProcessor();
        width = image.getWidth();

        SimpleIterativeLinearClustering silc = new SimpleIterativeLinearClustering(100, 5, 1);
        int labels[] = silc.fit(image);

        assertTrue(labels[getI(0, 0)] == labels[getI(0, 1)]);
        assertTrue(labels[getI(110, 70)] == labels[getI(238, 70)]);
        assertTrue(labels[getI(238, 70)] != labels[getI(239, 70)]);
        assertTrue(labels[getI(199, 0)] != labels[getI(200, 0)]);
    }

    private int getI(int x, int y) {
        return y * width + x;
    }
}
