/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.test.integration.listener;

import com.online.casino.domain.enums.GameEvent;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.online.casino.service.cometd.CometConstants.*;
import static org.junit.Assert.*;

/**
 * Created by Bjorn Harvold
 * Date: 3/10/11
 * Time: 3:41 PM
 * Responsibility: This class mimics the cometd client front-end channel. It will receive all the information
 * the javascript client would receive. In our test scenario it will act as the brains of the player.
 */
public class UserEventChannelListener implements ClientSessionChannel.MessageListener {
    private final static Logger log = LoggerFactory.getLogger(UserEventChannelListener.class);

    private final String pokergameId;
    private final String userId;
    private final String playerId;
    private final String username;

    public UserEventChannelListener(String userId, String playerId, String username, String pokergameId) {
        this.userId = userId;
        this.playerId = playerId;
        this.username = username;
        this.pokergameId = pokergameId;
    }

    @Override
    public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
        log.info("Received message on user channel: " + clientSessionChannel.getChannelId());
        Map<String, Object> map = message.getDataAsMap();

        String actionS = (String) map.get(EVENT);
        String incomingPokergameId = (String) map.get(POKER_GAME_ID);
        String status = (String) map.get(STATUS);

        assertEquals("Wrong pokergameId", pokergameId, incomingPokergameId);
        assertEquals("Status should be success", SUCCESS, status);
        assertEquals("Wrong action", GameEvent.QUEUE_PLAYER_EVENT, GameEvent.valueOf(actionS));
    }
}
