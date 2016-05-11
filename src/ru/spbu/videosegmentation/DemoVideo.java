package ru.spbu.videosegmentation;


import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

public class DemoVideo {
    public static void main(String[] args) {
        String input_filename = "data/demo2.mp4";
        String output_directory = "data";
        int segStep = 50;
        int maxIter = 5;
        double posWeight = 0.1;
        int mergingTolerance = 50;

        if(args.length > 0) {
            input_filename = args[0];
        } else if(args.length > 1) {
            output_directory = args[1];
        } else if(args.length > 2) {
            segStep = Integer.parseInt(args[2]);
        } else if(args.length > 3) {
            maxIter = Integer.parseInt(args[3]);
        } else if(args.length > 4) {
            posWeight = Double.parseDouble(args[4]);
        } else if(args.length > 5) {
            mergingTolerance = Integer.parseInt(args[5]);
        }

        VideoReader videoReader = new VideoReader(input_filename);

        int frameId = 0;
        while (videoReader.hasNext()) {
            ImagePlus imgPlus = new ImagePlus("", videoReader.getNext());
            ImageProcessor image = imgPlus.getProcessor();

            SimpleIterativeLinearClustering silc =
                    new SimpleIterativeLinearClustering(segStep, maxIter, posWeight, mergingTolerance);
            int labels[] = silc.fit(image);

            image = SegmentationVisualization.visualize(image, labels);
            imgPlus.setProcessor(image);

            FileSaver fs = new FileSaver(imgPlus);
            fs.saveAsPng(output_directory + "/" + frameId + ".png");
            frameId++;
        }
    }
}
