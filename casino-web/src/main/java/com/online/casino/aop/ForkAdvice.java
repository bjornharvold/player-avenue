/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Bjorn Harvold
 * Date: Aug 26, 2009
 * Time: 4:48:54 PM
 * Responsibility:
 */
public class ForkAdvice {
    private static final Logger log = LoggerFactory.getLogger(ForkAdvice.class);

    public void fork(final ProceedingJoinPoint pjp) {
        new Thread(new Runnable() {
            public void run() {
                log.info("Forking method execution: " + pjp);
                try {
                    pjp.proceed();
                } catch (Throwable t) {
                    // All we can do is logDispatchedView the error.
                    log.error(t.getMessage(), t);
                }
            }
        }).start();
    }
}
