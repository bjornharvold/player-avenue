/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.integration.listener;

import com.online.casino.domain.enums.GameAction;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.online.casino.service.cometd.CometConstants.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 3/9/11
 * Time: 8:23 PM
 * Responsibility: After authentication is successful, we use this class to subscribe to the pokergame channel
 */
public class SubscribePlayerToChannelsListener implements ClientSessionChannel.MessageListener {
    private final static Logger log = LoggerFactory.getLogger(SubscribePlayerToChannelsListener.class);
    private static final String USER_CHANNEL_ROOT = "/user";
    private static final String SERVICE_CHANNEL_ROOT = "/service/game";
    private final BayeuxClient bayeuxClient;
    private final String username;
    private final String userId;
    private final String pokergameId;
    private final String playerId;
    private final PokerGameChannelListener gameChannelListener;
    private final UserEventChannelListener userEventChannelListener;

    public SubscribePlayerToChannelsListener(String userId, String playerId, String username, String pokergameId, BayeuxClient bayeuxClient) {
        this.userId = userId;
        this.username = username;
        this.pokergameId = pokergameId;
        this.playerId = playerId;
        this.bayeuxClient = bayeuxClient;
        gameChannelListener = new PokerGameChannelListener();
        userEventChannelListener = new UserEventChannelListener(userId, playerId, username, pokergameId);
    }

    public void onMessage(ClientSessionChannel channel, Message message) {
        log.info("SubscribePlayerToChannelsListener in effect");

        assertTrue("Message.isSuccessful(): ", message.isSuccessful());
        if (message.isSuccessful()) {
            subscribeToPokerGameChannel();
        }
    }

    private void subscribeToPokerGameChannel() {
        bayeuxClient.batch(new Runnable() {
            public void run() {
                // this channel we publish commands to
                ClientSessionChannel userActionChannel = bayeuxClient.getChannel(USER_CHANNEL_ROOT + "/action/" + userId);

                // this channel we listen to
                ClientSessionChannel userEventChannel = bayeuxClient.getChannel(USER_CHANNEL_ROOT + "/event/" + userId);
                userEventChannel.addListener(userEventChannelListener);

                // this channel we only listen to
                ClientSessionChannel gameChannel = bayeuxClient.getChannel(SERVICE_CHANNEL_ROOT + "/" + pokergameId);
                gameChannel.addListener(gameChannelListener);

                // now let's join the game on the user channel
                // below is the required data successfully queue the player
                Map<String, Object> data = new HashMap<String, Object>();
                data.put(ACTION, GameAction.QUEUE_PLAYER_ACTION);
                data.put(POKER_GAME_ID, pokergameId);
                data.put(PLAYER_ID, playerId);
                data.put(BUYIN, new BigDecimal(100.5));
                data.put(MUST_HAVE_SEAT, false);
                data.put(SEAT_NUMBER, 1);

                log.info("Publishing a queue action to the user channel to get things started.");
                // send it across the user comet channel
                userActionChannel.publish(data);
            }
        });
    }
}
