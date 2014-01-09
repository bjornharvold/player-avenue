/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.enums.CmsType;
import com.online.casino.domain.enums.ContentType;
import com.online.casino.exception.CmsException;
import com.online.casino.service.CmsService;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/14/11
 * Time: 3:17 PM
 * Responsibility:
 */
@Service("cmsService")
public class CmsServiceImpl implements CmsService {

    /** Field description */
    private static final String AVATAR_DIRECTORY = "avatar";

    /** Field description */
    private static final String TEST_DIRECTORY = "test";

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(CmsServiceImpl.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${s3.domain}")
    private String s3Domain;

    /**
     * Field description
     */
    private final S3Service s3Service;

    private S3Bucket bucket = null;

    private AccessControlList acl = null;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param s3Service s3Service
     */
    @Autowired
    public CmsServiceImpl(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public void init() {
        try {
            bucket = s3Service.getBucket(bucketName);
            acl = s3Service.getBucketAcl(bucket);
        } catch (S3ServiceException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Method description
     *
     *
     * @param type type
     * @param contentType contentType
     * @param fileName fileName
     * @param bytes bytes
     *
     * @return Return value
     *
     * @throws CmsException CmsException
     */
    @Override
    public String upload(CmsType type, ContentType contentType, String fileName, byte[] bytes) throws CmsException {
        String result = null;

        if (bucket == null && acl == null) {
            init();
        }

        try {

            String relativeDirectoryPath = "";
            String cType                 = "";

            switch (type) {
                case AVATAR :
                    relativeDirectoryPath = AVATAR_DIRECTORY;
                    break;

                case TEST :
                    relativeDirectoryPath = TEST_DIRECTORY;
                    break;
            }

            switch (contentType) {
                case PNG:
                    cType = "image/png";

                    break;
                case JPG:
                    cType = "image/jpg";

                    break;
                case GIF:
                    cType = "image/gif";

                    break;

                case TEXT:
                    cType = "text/plain";
            }

            S3Object object = new S3Object(bucket, relativeDirectoryPath + "/" + fileName);

            object.setDataInputStream(new ByteArrayInputStream(bytes));
            object.setContentLength(bytes.length);
            object.setContentType(cType);
            object.setAcl(acl);

            s3Service.putObject(bucket, object);

//            StringBuilder sb = new StringBuilder("http://");
//            sb.append(bucketName);
//            sb.append(s3Domain);
            StringBuilder sb = new StringBuilder("/");
            sb.append(relativeDirectoryPath);
            sb.append("/");
            sb.append(fileName);

            result = sb.toString();
        } catch (S3ServiceException e) {
            throw new CmsException(e.getMessage(), e);
        }

        return result;
    }
}
