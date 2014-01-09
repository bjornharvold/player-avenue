/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: Bjorn Harvold
 * Date: Sep 11, 2009
 * Time: 3:09:59 PM
 * Responsibility:
 */
@Aspect
@Order(3)
@Component
public class RequestMappingAspect {
    private static final Logger log = LoggerFactory.getLogger(RequestMappingAspect.class);

    /**
     * execution point parameters
     * execution(@org.springframework.web.bind.annotation.RequestMapping java.lang.String *(..))
     * 1. method has annotation RequestMapping on it (optional)
     * 2. method access level public | private | protected (optional: leave empty for all)
     * 3. method returns a string
     * 4. any class and any method name
     * 5. any number of parameters
     */
    @Pointcut(value = "execution(@org.springframework.web.bind.annotation.RequestMapping java.lang.String *.*(..)) && @annotation(mapping)")
    public void logDispatchedView(RequestMapping mapping) {
    }

    @Before(value = "logDispatchedView(mapping)")
    public void before(JoinPoint jp, RequestMapping mapping) {
        log.info("User's request matches controller: " + jp.getSignature().getDeclaringType().getSimpleName() + " maps to: (" + getMapping(mapping) + "), using HTTP method: (" + getMethod(mapping) + ")" + getHeaders(mapping) + getParams(mapping));
    }

    private String getParams(RequestMapping mapping) {
        StringBuilder sb = new StringBuilder();

        if (mapping.params() != null && mapping.params().length > 0) {
            sb.append(", with headers: (");
            for (String s : mapping.params()) {
                sb.append(s);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }

        return sb.toString();
    }

    @AfterReturning(value = "logDispatchedView(mapping)", returning = "view")
    public void afterReturning(JoinPoint jp, RequestMapping mapping, Object view) throws Throwable {
        if (log.isTraceEnabled()) {
            log.trace("Dispatching view: " + view + ", for mapping: (" + getMapping(mapping) + ")");
        }
    }

    private String getHeaders(RequestMapping mapping) {
        StringBuilder sb = new StringBuilder();

        if (mapping.headers() != null && mapping.headers().length > 0) {
            sb.append(", with params: (");
            for (String s : mapping.headers()) {
                sb.append(s);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }

        return sb.toString();
    }

    private String getMethod(RequestMapping mapping) {
        StringBuilder sb = new StringBuilder();

        if (mapping.method() != null && mapping.method().length > 0) {
            for (RequestMethod s : mapping.method()) {
                sb.append(s.name());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    private String getMapping(RequestMapping mapping) {
        StringBuilder sb = new StringBuilder();

        if (mapping.value() != null && mapping.value().length > 0) {
            for (String s : mapping.value()) {
                sb.append(s);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
