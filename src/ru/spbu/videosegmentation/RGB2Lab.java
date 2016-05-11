package ru.spbu.videosegmentation;


public class RGB2Lab {
    public static double[] rgb2lab (double[] inputColor) {
        double[] RGB = new double[3];

        for(int i = 0; i < 3; i++) {
            RGB[i] = inputColor[i] / 255;
            if(RGB[i] > 0.04045) {
                RGB[i] = Math.pow((RGB[i] + 0.055) / 1.055, 2.4);
            } else {
                RGB[i] = RGB[i] / 12.92;
            }
            RGB[i] *= 100.;
        }

        double[] XYZ = new double[3];
        XYZ[0] = (RGB[0] * 0.4124 + RGB[1] * 0.3576 + RGB[2] * 0.1805) / 95.047;
        XYZ[1] = (RGB[0] * 0.2126 + RGB[1] * 0.7152 + RGB[2] * 0.0722) / 100.;
        XYZ[2] = (RGB[0] * 0.0193 + RGB[1] * 0.1192 + RGB[2] * 0.9505) / 108.883;

        for(int i = 0; i < 3; i++) {
            if(XYZ[i] > 0.008856) {
                XYZ[i] = Math.pow(XYZ[i], 1./3);
            } else {
                XYZ[i] = 7.787 * XYZ[i] + 16. / 116;
            }
        }

        double[] Lab = new double[3];
        Lab[0] = 116. * XYZ[1] - 16;
        Lab[1] = 500. * ( XYZ[0] - XYZ[1] );
        Lab[2] = 200. * ( XYZ[1] - XYZ[2] );

        return Lab;
    }
}
