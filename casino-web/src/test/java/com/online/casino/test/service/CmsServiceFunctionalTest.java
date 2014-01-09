/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.service;

import com.online.casino.domain.enums.CmsType;
import com.online.casino.domain.enums.ContentType;
import com.online.casino.service.CmsService;
import com.online.casino.utils.ImageResizer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * User: Bjorn Harvold
 * Date: Aug 30, 2009
 * Time: 1:56:40 PM
 * Responsibility:
 */
public class CmsServiceFunctionalTest extends AbstractFunctionalTest {
    private final static Logger log = LoggerFactory.getLogger(CmsServiceFunctionalTest.class);
    private static final String JPG = "jpg";

    @Autowired
    private CmsService cmsService;
    private final Resource resource = new ClassPathResource("profile.jpg");
    private final static String hw = "Hello World!";

    @Test
    public void testUploadTextFile() {
        log.info("Testing uploading a text file to CMS service...");

        try {
            log.info("Upload a text file");
            String result = cmsService.upload(CmsType.TEST, ContentType.TEXT, "helloworld.txt", hw.getBytes());

            log.info("Retrieved string from cms: " + result);
            assertNotNull("Return url from cms is missing", result);

        } catch (Exception e) {
            fail(e.getMessage());
        }

        log.info("Testing uploading a text file to CMS service complete!");
    }

    @Test
    public void testUploadImageFile() {
        log.info("Testing uploading a image file to CMS service...");

        try {
            log.info("Upload an image file");
            assertTrue("Image test resource doesn't exist", resource.exists());
            byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
            assertTrue("original byte[] is empty", bytes.length > 0);
            String result = cmsService.upload(CmsType.TEST, ContentType.JPG, "test.jpg", bytes);
            log.info("Retrieved string from cms: " + result);
            assertNotNull("Return url from cms is missing", result);

            log.info("Upload resized image file");
            InputStream resizedImage = ImageResizer.resize(resource.getInputStream(), 150, JPG);
            bytes = IOUtils.toByteArray(resizedImage);
            assertTrue("resized byte[] is empty", bytes.length > 0);
            result = cmsService.upload(CmsType.TEST, ContentType.JPG, "test_resized.jpg", bytes);
            log.info("Retrieved string from cms: " + result);
            assertNotNull("Return url from cms is missing", result);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        log.info("Testing uploading a image file to CMS service complete!");
    }
}
