/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 4/20/11
 * Time: 9:21 PM
 * Responsibility:
 */
//@Singleton
//@Named
//@Service("cometUserSubscriberService")
public class TimerTaskService {
    private final static Logger log = LoggerFactory.getLogger(TimerTaskService.class);

    private static final String CHANNEL = "/services/game/1";
//    @Inject
    private BayeuxServer bayeuxServer;

//    @Scheduled(fixedRate = 3000)
//    public void broadcastPing() {
//        bayeuxServer.createIfAbsent(CHANNEL, new ConfigurableServerChannel.Initializer() {
//            @Override
//            public void configureChannel(ConfigurableServerChannel channel) {
//                channel.addAuthorizer(GrantAuthorizer.GRANT_SUBSCRIBE);
//                channel.setPersistent(false);
//            }
//        });
//        ServerChannel channel = bayeuxServer.getChannel(CHANNEL);
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("time", new Date().toString());
//
//        log.info("Broadcasting to channel: " + CHANNEL);
//        channel.publish(null, map, null);
//    }

//    @Scheduled(fixedRate = 3000)
    public void broadcastPong() {
        bayeuxServer.createIfAbsent(CHANNEL, new GameChannelInitializer());
        ServerChannel channel = bayeuxServer.getChannel(CHANNEL);

        Map<String, String> map = new HashMap<String, String>();
        map.put("time", new Date().toString());

        log.info("Broadcasting to channel: " + CHANNEL);
        channel.publish(null, map, null);
    }
}
