package ru.spbu.videosegmentation;

import junit.framework.Assert;
import org.junit.Test;


public class RGB2LabTest extends Assert {
    @Test
    public void testBaseColors() {
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {0, 0, 0}), new double[] {0, 0, 0}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {255, 0, 0}), new double[] {53.2329, 80.1093, 67.2201}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {0, 255, 0}), new double[] {87.737, -86.1846, 83.1812}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {0, 0, 255}), new double[] {32.3026, 79.1967, -107.8637}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {0, 255, 255}), new double[] {91.1165, -48.0796, -14.1381}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {255, 0, 255}), new double[] {60.3199, 98.2542, -60.843}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {255, 255, 0}), new double[] {97.1382, -21.5559, 94.4825}));
        assertTrue(isEquals(RGB2Lab.rgb2lab(new double[] {255, 255, 255}), new double[] {100.0, 0.0053, -0.0104}));
    }

    private boolean isEquals(double[] a, double[] b) {
        double tolerance = 1e-4;

        for(int i = 0; i < a.length; i++) {
            if(Math.abs(a[i] - b[i]) > tolerance) {
                return false;
            }
        }
        return true;
    }
}
