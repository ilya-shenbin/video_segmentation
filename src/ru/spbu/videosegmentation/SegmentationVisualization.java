package ru.spbu.videosegmentation;

import ij.process.ImageProcessor;

import java.awt.*;

public class SegmentationVisualization {
    private static int width;

    static public ImageProcessor visualize(ImageProcessor image, int[] labels) {
        int size = image.getPixelCount();
        width = image.getWidth();

        ImageProcessor result = image.duplicate();

        result.setColor(new Color(255, 0, 0));
        for(int i = 0; i < size; i++) {
            if(i % width != 0 && labels[i] != labels[i-1]) {
                result.drawDot(getX(i) - 1, getY(i));
                result.drawDot(getX(i), getY(i));
            }

            if(i > width && labels[i] != labels[i-width]) {
                result.drawDot(getX(i), getY(i) - 1);
                result.drawDot(getX(i), getY(i));
            }
        }

        return result;
    }

    private static int getX(int i) {
        return i % width;
    }

    private static int getY(int i) {
        return i / width;
    }
}
