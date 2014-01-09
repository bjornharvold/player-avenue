/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.service.cometd;

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.security.SpringSecurityHelper;
import org.apache.commons.lang.StringUtils;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.Authorizer;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.online.casino.service.cometd.CometConstants.USER_ROOT_CHANNEL;

/**
 * Created by Bjorn Harvold
 * Date: 4/17/11
 * Time: 4:34 PM
 * Responsibility:
 */
public class UserChannelAuthorizer implements Authorizer {
    private final static Logger log = LoggerFactory.getLogger(UserChannelAuthorizer.class);

    @Override
    public Result authorize(Operation operation, ChannelId channelId, ServerSession serverSession,
                            ServerMessage serverMessage) {
        Result result = Result.ignore();

        if (log.isDebugEnabled()) {
            log.debug("Running user channel authorizer...");
        }

        // retrieve the logged in user who is trying to access this channel
        ApplicationUser principal = SpringSecurityHelper.getSecurityContextPrincipal();
        boolean isLocalSession = serverSession.isLocalSession();
        boolean isUserActionChannel = channelId.toString().startsWith(USER_ROOT_CHANNEL + "/action/");
        boolean isUserEventChannel = channelId.toString().startsWith(USER_ROOT_CHANNEL + "/event/");

        if (log.isDebugEnabled()) {
            log.debug("Channel is a user channel: " + channelId);
        }
        if (principal != null) {
            if (log.isDebugEnabled()) {
                log.debug("Principal is available to determine channel privileges. Principal ID: " + principal.getId());
            }
            // the owner can create and publish on an action channel but not subscribe
            if (isUserActionChannel && (operation.equals(Operation.CREATE) || operation.equals(Operation.PUBLISH))) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel is an action channel. Principal can: " + Operation.CREATE + " and " + Operation.PUBLISH + " on " + channelId);
                }
                result = Result.grant();
            } else if (isUserEventChannel && (operation.equals(Operation.CREATE) || operation.equals(Operation.SUBSCRIBE))) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel is an event channel. Principal can: " + Operation.CREATE + " and " + Operation.SUBSCRIBE + " on " + channelId);
                }
                result = Result.grant();
            }
        }

        if (isLocalSession) {
            if (log.isDebugEnabled()) {
                log.debug("This is a local session");
            }
            if (isUserActionChannel && (operation.equals(Operation.CREATE) || operation.equals(Operation.SUBSCRIBE))) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel is an action channel. LocalSession can: " + Operation.CREATE + " and " + Operation.SUBSCRIBE + " on " + channelId);
                }
                result = Result.grant();
            } else if (isUserEventChannel && (operation.equals(Operation.CREATE) || operation.equals(Operation.PUBLISH))) {
                if (log.isDebugEnabled()) {
                    log.debug("Channel is an event channel. LocalSession can: " + Operation.CREATE + " and " + Operation.PUBLISH + " on " + channelId);
                }
                result = Result.grant();
            }
        }


        return result;
    }
}
