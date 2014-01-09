/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.cometd;

//~--- non-JDK imports --------------------------------------------------------

import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimestampExtension;
import org.cometd.server.ext.TimesyncExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.ServletContext;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * The Spring component that configures CometD services.
 *
 * Spring scans the classes and finds this class annotated with Spring's @Component
 * annotation, and makes an instance. Then it notices that it has a bean factory
 * method (annotated with @Bean) that produces the BayeuxServer instance.
 * Note that, as per Spring documentation, this class is subclassed by Spring
 * via CGLIB to invoke bean factory methods only once.
 *
 * Implementing {@link DestructionAwareBeanPostProcessor} allows to plug-in
 * CometD's annotation processor to configure the CometD services.
 */
@Component
public class Configurator implements DestructionAwareBeanPostProcessor, ServletContextAware {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(Configurator.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private BayeuxServer bayeuxServer;

    /** Field description */
    private ServerAnnotationProcessor processor;

    @Autowired
    private BayeuxAuthenticator bayeuxAuthenticator;

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public BayeuxServer bayeuxServer() {
        BayeuxServerImpl bean = new BayeuxServerImpl();

        bean.setOption(BayeuxServerImpl.LOG_LEVEL, "3");
        bean.setOption(BayeuxServerImpl.JSON_CONTEXT, "org.cometd.server.JacksonJSONContextServer");
        bean.setSecurityPolicy(bayeuxAuthenticator);

        // register extensions
        bean.addExtension(new AcknowledgedMessagesExtension());
        bean.addExtension(new TimestampExtension());
        bean.addExtension(new TimesyncExtension());

        log.info("Created new BayeuxServer instance: " + bean);

        return bean;
    }

    /**
     * Method description
     *
     *
     * @param bean bean
     * @param name name
     *
     * @return Return value
     *
     * @throws BeansException BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    /**
     * Method description
     *
     *
     * @param bean bean
     * @param name name
     *
     * @throws BeansException BeansException
     */
    public void postProcessBeforeDestruction(Object bean, String name) throws BeansException {
        processor.deprocessCallbacks(bean);
    }

    /**
     * Method description
     *
     *
     * @param bean bean
     * @param name name
     *
     * @return Return value
     *
     * @throws BeansException BeansException
     */
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        processor.processDependencies(bean);
        processor.processConfigurations(bean);
        processor.processCallbacks(bean);

        return bean;
    }

    /**
     * Method description
     *
     */
    @PreDestroy
    private void destroy() {
        log.info("Closing Bayeux Configurator");
    }

    /**
     * Method description
     *
     */
    @PostConstruct
    private void init() {
        log.info("Configurator.init()");
        this.processor = new ServerAnnotationProcessor(bayeuxServer);
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param bayeuxServer bayeuxServer
     */
    @Inject
    private void setBayeuxServer(BayeuxServer bayeuxServer) {
        this.bayeuxServer = bayeuxServer;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bayeuxServer);
    }
}
