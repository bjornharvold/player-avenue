/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.cometd;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.security.SpringSecurityHelper;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.DefaultSecurityPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/24/10
 * Time: 10:57 PM
 * Responsibility:
 */
@Component("bayeuxAuthenticator")
public class BayeuxAuthenticator extends DefaultSecurityPolicy implements ServerSession.RemoveListener {
    private final static Logger log = LoggerFactory.getLogger(BayeuxAuthenticator.class);


    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param server  server
     * @param session session
     * @param message message
     * @return Return value
     */
    @Override
    public boolean canHandshake(BayeuxServer server, ServerSession session, ServerMessage message) {
        boolean result = false;
        ApplicationUser principal = SpringSecurityHelper.getSecurityContextPrincipal();

        if (log.isDebugEnabled()) {
            log.debug("Bayeux security handshake requested...");
        }

        if (session.isLocalSession()) {
            // immediate true if this is a local session
            result = true;
        } else if (principal != null) {
            // in the real service we will be relying on the user being logged in
            result = true;
            // Be notified when the session disappears
            session.addListener(this);
        }

        // Authentication successful
        if (result) {
            if (log.isDebugEnabled()) {
                log.debug("Bayeux security handshake succeeded with serverSession: " + session.toString());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Bayeux security handshake failed with serverSession: " + session.toString());
            }
        }


        return result;
    }

    @Override
    public boolean canPublish(BayeuxServer server, ServerSession session, ServerChannel channel, ServerMessage message) {
        if (log.isDebugEnabled()) {
            log.debug("Bayeux security canPublish requested from session: " + session.toString() + " on channel: " + channel.toString());
        }

        boolean result = super.canPublish(server, session, channel, message);

        if (log.isDebugEnabled()) {
            log.debug("canPublish: " + result);
        }

        return result;
    }

    @Override
    public boolean canSubscribe(BayeuxServer server, ServerSession session, ServerChannel channel, ServerMessage message) {
        if (log.isDebugEnabled()) {
            log.debug("Bayeux security canSubscribe requested from session: " + session.toString() + " on channel: " + channel.toString());
        }

        boolean result = super.canPublish(server, session, channel, message);

        if (log.isDebugEnabled()) {
            log.debug("canSubscribe: " + result);
        }

        return result;
    }

    /**
     * Method description
     *
     * @param session session
     * @param expired expired
     */
    public void removed(ServerSession session, boolean expired) {
        if (log.isDebugEnabled()) {
            log.debug("Calling remove() for session: " + session.toString() + ". Expired: " + expired);
        }
    }
}
