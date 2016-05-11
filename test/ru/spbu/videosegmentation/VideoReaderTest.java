package ru.spbu.videosegmentation;


import junit.framework.Assert;
import org.junit.Test;

public class VideoReaderTest extends Assert {
    @Test
    public void testFramesCounter() {
        VideoReader videoReader = new VideoReader("data/demo2.mp4");
        int count = 0;
        while (videoReader.hasNext()) {
            videoReader.getNext();
            count++;
        }

        assertEquals(40, count);
    }
}
