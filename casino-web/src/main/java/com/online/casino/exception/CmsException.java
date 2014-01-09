package com.online.casino.exception;

/**
 * User: bjorn
 * Date: Oct 1, 2008
 * Time: 1:34:50 PM
 */
public class CmsException extends AbstractException {
    private static final long serialVersionUID = 2699648934062918893L;

    public CmsException() {
        super();
    }

    public CmsException(String s) {
        super(s);
    }

    public CmsException(String s, String ... params) {
        super(s, params);
    }

    public CmsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CmsException(String s, Throwable throwable, String ... params) {
        super(s, throwable, params);
    }
}
