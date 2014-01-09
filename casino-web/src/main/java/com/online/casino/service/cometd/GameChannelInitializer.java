/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bjorn Harvold
 * Date: 4/17/11
 * Time: 3:56 PM
 * Responsibility:
 */
public class GameChannelInitializer implements ConfigurableServerChannel.Initializer {
    private final static Logger log = LoggerFactory.getLogger(GameChannelInitializer.class);

    @Override
    public void configureChannel(ConfigurableServerChannel channel) {
        channel.addAuthorizer(new GameChannelAuthorizer());
        channel.setPersistent(false);
    }
}
