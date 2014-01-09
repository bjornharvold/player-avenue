/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.test.integration.listener;

//~--- non-JDK imports --------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 3/9/11
 * Time: 8:25 PM
 * Responsibility:
 */

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        ${project.version}, 11/03/09
 * @author         Bjorn Harvold
 */
public class ConnectionListener implements ClientSessionChannel.MessageListener {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(ConnectionListener.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final BayeuxClient bayeuxClient;

    /** Field description */
    private boolean connected;

    /** Field description */
    private final String username;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param username username
     * @param bayeuxClient bayeuxClient
     */
    public ConnectionListener(String username, BayeuxClient bayeuxClient) {
        this.username     = username;
        this.bayeuxClient = bayeuxClient;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param channel channel
     * @param message message
     */
    public void onMessage(ClientSessionChannel channel, Message message) {
        log.info("ConnectionListener received a message for username: " + username);

        if (bayeuxClient.isDisconnected()) {
            connected = false;
            connectionClosed();

            return;
        }

        boolean wasConnected = connected;

        connected = message.isSuccessful();

        if (!wasConnected && connected) {
            connectionEstablished();
        } else if (wasConnected && !connected) {
            connectionBroken();
        }
    }

    /**
     * Method description
     *
     */
    private void connectionBroken() {
        log.info("Connection to Server Broken");
    }

    /**
     * Method description
     *
     */
    private void connectionClosed() {
        log.info("Connection to Server Closed");
    }

    /**
     * Method description
     *
     */
    private void connectionEstablished() {
        log.info("Connection to Server Opened");

//      Map<String, Object> data = new HashMap<String, Object>();
//      data.put("user", nickname);
//      data.put("room", "/chat/demo");
//      client.getChannel("/service/members").publish(data);
    }
}
