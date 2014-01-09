/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.cometd;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.enums.GameAction;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.FiniteStateMachineClientService;
import com.online.casino.service.fsm.event.CallEvent;
import com.online.casino.service.fsm.event.CheckEvent;
import com.online.casino.service.fsm.event.FoldEvent;
import com.online.casino.service.fsm.event.LeaveEvent;
import com.online.casino.service.fsm.event.QueuePlayerEvent;
import com.online.casino.service.fsm.event.RaiseEvent;
import com.online.casino.service.fsm.event.ReQueuePlayerEvent;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.online.casino.service.cometd.CometConstants.ACTION;
import static com.online.casino.service.cometd.CometConstants.AMOUNT;
import static com.online.casino.service.cometd.CometConstants.BUYIN;
import static com.online.casino.service.cometd.CometConstants.GAMBLER_ID;
import static com.online.casino.service.cometd.CometConstants.HAND_ID;
import static com.online.casino.service.cometd.CometConstants.MUST_HAVE_SEAT;
import static com.online.casino.service.cometd.CometConstants.PLAYER_ID;
import static com.online.casino.service.cometd.CometConstants.POKER_GAME_ID;
import static com.online.casino.service.cometd.CometConstants.SEAT_NUMBER;
import static com.online.casino.service.cometd.CometConstants.SERVICE_USER_ACTION_CHANNEL;
import static com.online.casino.service.cometd.CometConstants.SERVICE_USER_CHANNEL;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Nov 23, 2009
 * Time: 9:45:24 AM
 * Responsibility: This is the user user channel where every user has his own channel for all private communication
 * with the server.
 */
@Singleton
@Named
@Service("cometUserSubscriberService")
public class CometUserSubscriberService {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(CometUserSubscriberService.class);

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @Inject
    private BayeuxServer bayeuxServer;

    @Inject
    private FiniteStateMachineClientService finiteStateMachineClientService;

    /**
     * Field description
     */
    @Session
    private ServerSession serverSession;

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     */
    @PreDestroy
    public void destroy() {
        log.info("Shutting down Comet User Subscriber Service");
    }

    /**
     * Method description
     */
    @PostConstruct
    public void init() {
        log.info("Comet User Subscriber Service Initialized");
    }

    /**
     * Method description
     *
     * @param client  client
     * @param message message
     */
    @Listener(SERVICE_USER_ACTION_CHANNEL)
    public void listenAction(ServerSession client, ServerMessage.Mutable message) {
        if (log.isDebugEnabled()) {
            log.debug("Comet User Subscriber Service was executed");
        }

        ApplicationUser principal = SpringSecurityHelper.getSecurityContextPrincipal();
        boolean isLocalSession = client.isLocalSession();

        if (principal == null && !isLocalSession) {
            throw new IllegalStateException("Missing session security object");
        }

        // this is the json map we get from the server
        Map<String, Object> map = message.getDataAsMap();
        Map<String, String> result = new HashMap<String, String>();

        // validate that the user has provided an action key
        validateFields(map, ACTION, POKER_GAME_ID);

        // based on the data user sends us we do different things game related
        final String actionS = (String) map.get(ACTION);
        final String pokergameId = (String) map.get(POKER_GAME_ID);

        final String applicationUserId = principal.getId();
        final String gamblerId = (String) map.get(GAMBLER_ID);

        final String handId = (String) map.get(HAND_ID);

        try {
            // show what the user is trying to do
            printDebugInformation(message, actionS);

            GameAction action = GameAction.valueOf(actionS);

            switch (action) {
                case QUEUE_PLAYER_ACTION:
                    validateFields(map, PLAYER_ID, BUYIN, MUST_HAVE_SEAT, SEAT_NUMBER);
                    final String playerId = (String) map.get(PLAYER_ID);
                    final BigDecimal buyin = new BigDecimal(map.get(BUYIN).toString());
                    final Boolean mustHaveSeat = Boolean.parseBoolean((String) map.get(MUST_HAVE_SEAT));
                    final Integer seatNumber = Integer.parseInt(map.get(SEAT_NUMBER).toString());

                    finiteStateMachineClientService.dispatchQueuePlayerEvent(new QueuePlayerEvent(pokergameId, applicationUserId, playerId, buyin, mustHaveSeat, seatNumber));
                    break;
                case CALL_ACTION:
                    validateFields(map, HAND_ID, GAMBLER_ID);

                    finiteStateMachineClientService.dispatchCallEvent(new CallEvent(pokergameId, handId, applicationUserId, gamblerId));
                    break;
                case REQUEUE_PLAYER_ACTION:
                    validateFields(map, GAMBLER_ID);

                    finiteStateMachineClientService.dispatchReQueuePlayerEvent((new ReQueuePlayerEvent(pokergameId, applicationUserId, gamblerId)));
                    break;
                case CHECK_ACTION:
                    validateFields(map, HAND_ID, GAMBLER_ID);

                    finiteStateMachineClientService.dispatchCheckEvent(new CheckEvent(pokergameId, handId, applicationUserId, gamblerId));
                    break;
                case RAISE_ACTION:
                    validateFields(map, HAND_ID, GAMBLER_ID, AMOUNT);
                    BigDecimal amount = new BigDecimal((String) map.get(AMOUNT));

                    finiteStateMachineClientService.dispatchRaiseEvent(new RaiseEvent(pokergameId, handId, applicationUserId, gamblerId, amount));
                    break;
                case FOLD_ACTION:
                    validateFields(map, HAND_ID, GAMBLER_ID);

                    finiteStateMachineClientService.dispatchFoldEvent(new FoldEvent(pokergameId, handId, applicationUserId, gamblerId));
                    break;
                case LEAVE_ACTION:
                    validateFields(map, PLAYER_ID);
                    final String playerId2 = (String) map.get(PLAYER_ID);

                    finiteStateMachineClientService.dispatchLeaveEvent(new LeaveEvent(pokergameId, applicationUserId, playerId2, handId, gamblerId));
                    break;
                default:
                    if (log.isErrorEnabled()) {
                        log.error("There is no such thing as a default action allowed here.");
                    }
            }
        } catch (IllegalArgumentException ex) {
            if (log.isErrorEnabled()) {
                log.error(ex.getMessage(), ex);
            }
        } catch (NullPointerException ex) {
            if (log.isErrorEnabled()) {
                log.error(ex.getMessage(), ex);
            }
        }

// nothing is returned here. The broadcast service will broadcast back an acknowledgement message
//        client.deliver(serverSession, message.getChannel(), result, null);
    }

    private void printDebugInformation(ServerMessage.Mutable message, String action) {
        if (log.isDebugEnabled()) {
            log.debug("User on channel: " + message.getChannel() + " wants to: " + action);
        }
    }

    /**
     * Here we configure the user channel. The root channel is untouchable. Child channels can be created if
     * the child channel id matches that of the user's natural key.
     *
     * @param channel channel
     */
    @Configure({SERVICE_USER_CHANNEL})
    private void configureUserServiceStarStar(final ConfigurableServerChannel channel) {

        // default is none
        channel.addAuthorizer(GrantAuthorizer.GRANT_NONE);

        channel.addAuthorizer(new UserChannelAuthorizer());

        channel.setPersistent(false);
    }

    /**
     * Utility method to validate that certain fields are available before kicking off the actual event
     *
     * @param map    map from cometd
     * @param fields fields to validate
     */
    private void validateFields(Map<String, Object> map, String... fields) {
        for (String field : fields) {
            if (!map.containsKey(field)) {
                String warning = "Missing " + field + " key in user json data.";
                if (log.isWarnEnabled()) {
                    log.warn(warning);
                }

                throw new IllegalArgumentException(warning);
            }
        }
    }
}
