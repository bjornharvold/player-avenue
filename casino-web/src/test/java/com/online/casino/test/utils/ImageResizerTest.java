/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.utils;

import com.online.casino.utils.ImageResizer;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Bjorn Harvold
 * Date: 3/27/11
 * Time: 8:32 PM
 * Responsibility:
 */
public class ImageResizerTest extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(ImageResizerTest.class);
    private final Resource resource = new ClassPathResource("profile.jpg");
    private final static String file = "/Users/crash/resized_image1.jpg";
    private final static String file2 = "/Users/crash/resized_image2.jpg";

    @Test
    public void testImageResizer() {
        log.info("Testing ImageResizer...");
        assertTrue("Image test resource doesn't exist", resource.exists());

        try {
            BufferedImage bi = ImageIO.read(resource.getInputStream());

            assertEquals("Width of source image is incorrect: ", 400, bi.getWidth());
            InputStream is = ImageResizer.resize(resource.getInputStream(), 150, "jpg");
            BufferedImage resized = ImageIO.read(is);
            assertEquals("Width of resized image is incorrect: ", 148, resized.getWidth());

            // write to file for test only
//            ImageIO.write(resized, "jpg", new File(file));
//            IOUtils.closeQuietly(is);
//            FileWriter pw = new FileWriter(file2, false);
//            IOUtils.copy(is, pw);

//            is = ImageResizer.resize(resource.getInputStream(), 150, "jpg");
//            byte[] bytes = IOUtils.toByteArray(is);
//            FileUtils.writeByteArrayToFile(new File(file2), bytes);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        log.info("Testing ImageResizer complete!");
    }
}
