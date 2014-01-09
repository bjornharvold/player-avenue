/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.integration;

import com.online.casino.test.integration.listener.ConnectionListener;
import com.online.casino.test.integration.listener.SubscribePlayerToChannelsListener;
import junit.framework.TestCase;
import org.cometd.bayeux.Channel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.ext.TimesyncClientExtension;
import org.cometd.client.transport.LongPollingTransport;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 2/20/11
 * Time: 10:58 PM
 * Responsibility: This integration test represents, as close to as possible, a real live Texas Hold-em No Limit
 * scenario through cometd channels. In this scenario, we'll have two players playing the game.
 */
public class CometTHNLCallsOnly2PersonFunctionalTestNOTREADY extends TestCase {
    private final static Logger log = LoggerFactory.getLogger(CometTHNLCallsOnly2PersonFunctionalTestNOTREADY.class);
    private Map<String, BayeuxClient> bayeuxClients = new HashMap<String, BayeuxClient>();
    private final static String handshakeUrl = "http://localhost:9080/cometd";
    private final static String player1_system_user_id = "4";
    private final static String player1_player_id = "4";
    private final static String player1_username = "player1";
    private final static String player1_password = "player1";
    private final static String player2_system_user_id = "5";
    private final static String player2_player_id = "7";
    private final static String player2_username = "player2";
    private final static String player2_password = "player2";
    private final static String pokergameId = "1";

    private void handshakeWithServer(String userId, String playerId, String username, String password) {
//        Map<String, Object> authentication = new HashMap<String, Object>();
//        authentication.put("username", username);
//        authentication.put("password", password);
//
//        // our authentication map
//        Map<String, Object> ext = new HashMap<String, Object>();
//        ext.put("authentication", authentication);
//        ext.put("ack", Boolean.TRUE);
//
//        Map<String, Object> handshakeFields = new HashMap<String, Object>();
//        handshakeFields.put("ext", ext);

        // start bayeux client
        BayeuxClient bayeuxClient = new BayeuxClient(handshakeUrl, LongPollingTransport.create(null));
        bayeuxClient.getChannel(Channel.META_HANDSHAKE).addListener(new SubscribePlayerToChannelsListener(userId, playerId, username, pokergameId, bayeuxClient));
        bayeuxClient.getChannel(Channel.META_CONNECT).addListener(new ConnectionListener(username, bayeuxClient));
        bayeuxClient.addExtension(new TimesyncClientExtension());

        bayeuxClient.handshake();
        boolean success = bayeuxClient.waitFor(1000, BayeuxClient.State.CONNECTED);

        if (!success) {
            fail("Could not handshake with server at: " + handshakeUrl);
        } else {
            log.info("Cometd server handshake succeeded for username: " + username);
            log.info("Adding successful handshake client to map with key: " + username);
            bayeuxClients.put(username, bayeuxClient);
        }
    }

    @After
    public void tearDown() {
        log.info("Disconnecting from cometd channel");

        if (bayeuxClients != null) {
            for (String username : bayeuxClients.keySet()) {
                log.info("Disconnecting user: " + username);
                bayeuxClients.get(username).disconnect();
            }
        }
    }

    @Test
    public void testCometDGameChannels() {
        log.info("Testing the pokergame cometd channel");

        log.info("Getting ready for cometd server handshake...");

        // register players for game
        handshakeWithServer(player1_system_user_id, player1_player_id, player1_username, player1_password);
        handshakeWithServer(player2_system_user_id, player2_player_id, player2_username, player2_password);

        assertEquals("Expecting 2 bayeuxClients", 2, bayeuxClients.size());

        log.info("We are ready for the game");

        log.info("Testing the pokergame cometd channel complete");
    }
}
