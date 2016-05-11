package ru.spbu.videosegmentation;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;


public class DemoImage {
    public static void main(String[] args) {
        String input_filename = "data/demo.jpg";
        String output_filename = "data/demo_out.png";

        ImagePlus imgPlus = new ImagePlus(input_filename);
        ImageProcessor image = imgPlus.getProcessor();

        SimpleIterativeLinearClustering silc = new SimpleIterativeLinearClustering(100, 5, 0.1, 50);
        int labels[] = silc.fit(image);

        image = SegmentationVisualization.visualize(image, labels);
        imgPlus.setProcessor(image);

        FileSaver fs = new FileSaver(imgPlus);
        fs.saveAsPng(output_filename);
    }
}
