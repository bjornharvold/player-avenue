/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Bjorn Harvold
 * Date: Nov 23, 2009
 * Time: 9:45:24 AM
 * Responsibility:
 */
@Singleton
@Named
@Service("echoService")
public class EchoService {
    private final static Logger log = LoggerFactory.getLogger(EchoService.class);

    @Inject
    private BayeuxServer bayeux;

    @Session
    private ServerSession serverSession;

    @PostConstruct
    public void init() {
        log.info("Echo Service Initialized");
    }

    @Listener("/service/echo")
    public void echo(ServerSession remote, ServerMessage.Mutable message) {
        if (log.isDebugEnabled()) {
            log.debug("Echo service was executed");
        }
        String channel = message.getChannel();
        Map<String, String> data = (Map<String, String>) message.getData();
        String echo = data.get("echo");
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("echo", "You said: " + echo);

        remote.deliver(serverSession, channel, output, null);
    }
}
