package com.online.casino.service;

import com.online.casino.domain.enums.CmsType;
import com.online.casino.domain.enums.ContentType;
import com.online.casino.exception.CmsException;

/**
 * User: bjorn
 * Date: Oct 1, 2008
 * Time: 4:20:11 PM
 */
public interface CmsService {

    String upload(CmsType type, ContentType contentType, String fileName, byte[] bytes) throws CmsException;
}
