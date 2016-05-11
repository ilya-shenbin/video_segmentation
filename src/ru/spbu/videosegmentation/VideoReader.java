package ru.spbu.videosegmentation;


import com.xuggle.xuggler.*;

import java.awt.image.BufferedImage;

public class VideoReader {
    private IContainer container;
    private int videoStreamId;
    private IStreamCoder videoCoder;
    private IPacket packet;
    private BufferedImage nextImage = null;

    public VideoReader(String filename) {
        container = IContainer.make();
        if (container.open(filename, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("could not open file: " + filename);
        int numStreams = container.getNumStreams();
        videoStreamId = -1;
        videoCoder = null;
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        videoCoder.open();
        packet = IPacket.make();
    }

    public boolean hasNext() {
        while (nextImage == null && container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(
                        videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight()
                );
                int offset = 0;
                while (offset < packet.getSize()) {
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                    offset += bytesDecoded;
                    if (picture.isComplete()) {
                        nextImage = Utils.videoPictureToImage(picture);
                        break;
                    }
                }
            }
        }

        return nextImage != null;
    }

    public BufferedImage getNext() {
        BufferedImage result = nextImage;
        nextImage = null;
        return result;
    }

    protected void finalize() {
        videoCoder.close();
        container.close();
        try {
            super.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
