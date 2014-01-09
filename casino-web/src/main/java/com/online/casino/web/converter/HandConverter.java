package com.online.casino.web.converter;

import com.online.casino.domain.entity.Gambler;
import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.domain.entity.Hand;
import com.online.casino.domain.entity.Stake;
import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Bjorn Harvold
 * Date: Mar 21, 2010
 * Time: 1:40:17 PM
 * Responsibility: Converts Hand entity to smaller footprint object cometd supports
 */
public class HandConverter implements CustomConverter {
    private final static Logger log = LoggerFactory.getLogger(HandConverter.class);

    @Override
    public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {
        if (source == null) {
            return null;
        }
        Map<String, Object> dest = null;

        if (source instanceof Hand) {
            Hand hand = (Hand) source;
            // check to see if the object already exists
            if (destination == null) {
                dest = new HashMap<String, Object>();
            } else {
                dest = (HashMap<String, Object>) destination;
            }
            
            dest.put("tableSnapshot", convertHand(hand));
            return dest;
        } else if (source instanceof Map) {
            log.error("This is a one-way converter. You cannot convert a map to a hand entity. Only the other way around.");
            return null;
        } else {
            throw new MappingException("Converter HandConverter used incorrectly. Arguments passed in were:" + destination + " and " + source);
        }

    }

    private List<Map<String, Object>> convertHand(Hand hand) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("buttonSeat", hand.getDealerSeat());
        map.put("activeSeat", hand.getCurrentGamblerSeat());
        map.put("stage", hand.getStatus().name().toLowerCase());
        map.put("flop", hand.getFlop());
        map.put("turn", hand.getTurn());
        map.put("river", hand.getRiver());
        map.put("table", convertGame(hand.getGame().getTemplate()));
        map.put("seats", convertGamblers(hand.getGamblers()));


        result.add(map);
        return result;
    }

    private List<Map<String, Object>> convertGamblers(List<Gambler> gamblers) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (Gambler g : gamblers) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", g.getId());
            map.put("name", g.getPlayer().getNickname());
            map.put("stake", 0);
            map.put("state", g.getStatus());
            map.put("seatNumber", g.getSeatNumber());

            result.add(map);
        }

        return result;
    }

    private List<Map<String, Object>> convertGame(GameTemplate template) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", template.getLimitType().name().toLowerCase());
        map.put("currency", template.getCasino().getCurrency().name().toLowerCase());
        map.put("blinds", convertStake(template.getStake()));

        result.add(map);
        return result;
    }

    private List<Map<String, Object>> convertStake(Stake stake) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("small", stake.getLow());
        map.put("high", stake.getHigh());
        result.add(map);

        return result;
    }
}
