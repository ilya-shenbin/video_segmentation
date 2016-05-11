package ru.spbu.videosegmentation;


import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

public class DemoVideo {
    public static void main(String[] args) {
        String filename = "data/demo2.mp4";

        VideoReader videoReader = new VideoReader(filename);

        int frameId = 0;
        while (videoReader.hasNext()) {
            ImagePlus imgPlus = new ImagePlus("filename", videoReader.getNext());
            ImageProcessor image = imgPlus.getProcessor();

            SimpleIterativeLinearClustering silc = new SimpleIterativeLinearClustering(50, 5, 0.01, 30);
            int labels[] = silc.fit(image);

            image = SegmentationVisualization.visualize(image, labels);
            imgPlus.setProcessor(image);

            FileSaver fs = new FileSaver(imgPlus);
            fs.saveAsPng("data/res/" + frameId + ".png");
            frameId++;
        }
    }
}
