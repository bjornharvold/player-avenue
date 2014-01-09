/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.Authorizer;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bjorn Harvold
 * Date: 4/17/11
 * Time: 4:34 PM
 * Responsibility:
 */
public class GameChannelAuthorizer implements Authorizer {
    private final static Logger log = LoggerFactory.getLogger(GameChannelAuthorizer.class);

    @Override
    public Result authorize(Operation operation, ChannelId channelId, ServerSession serverSession,
                            ServerMessage serverMessage) {
        Result result = Result.ignore();

        if (log.isDebugEnabled()) {
            log.debug("Running game channel authorizer...");
        }

        boolean isLocalSession = serverSession.isLocalSession();

        if (isLocalSession && operation.equals(Operation.CREATE) || operation.equals(Operation.PUBLISH)) {
            if (log.isDebugEnabled()) {
                log.debug("LocalSession can " + Operation.CREATE + " and " + Operation.PUBLISH + " on " + channelId);
            }
            result = Result.grant();

        } else if (operation.equals(Operation.SUBSCRIBE)) {
            if (log.isDebugEnabled()) {
                log.debug(Operation.SUBSCRIBE + " request granted automatically on game channel: " + channelId);
            }
            result = Result.grant();
        }

        return result;
    }
}
