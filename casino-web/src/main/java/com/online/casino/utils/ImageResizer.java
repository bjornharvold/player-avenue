package com.online.casino.utils;

import com.thebuzzmedia.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;

/**
 * User: bjorn
 * Date: Oct 4, 2008
 * Time: 5:18:29 PM
 */
public class ImageResizer {

    /**
     * Resizes an image quickly
     *
     * @param image
     * @param desiredWidth
     * @param extension
     * @return resized input stream
     * @throws IOException
     */
    public synchronized static InputStream resize(final InputStream image, int desiredWidth, String extension) throws IOException {
        InputStream result;

        BufferedImage bi = ImageIO.read(image);
        int currentWidth = bi.getWidth();

        if (currentWidth > desiredWidth) {
            // time to resize
//            BufferedImage resized = GraphicsUtilities.createThumbnailFast(bi, desiredWidth);
            BufferedImage resized = Scalr.resize(bi, desiredWidth);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resized, extension, bos);

            result = new ByteArrayInputStream(bos.toByteArray());
        } else {
            // no resize needed
            // TODO look into why I bothered writing this piece of code instead of doing nothing here
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, extension, bos);

            result = new ByteArrayInputStream(bos.toByteArray());
        }

        return result;
    }
}
