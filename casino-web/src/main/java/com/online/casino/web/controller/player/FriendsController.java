/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */



package com.online.casino.web.controller.player;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.entity.Relationship;
import com.online.casino.domain.enums.RelationshipStatus;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
import com.online.casino.service.RelationshipService;
import com.online.casino.web.WebConstants;
import com.online.casino.web.utils.SimpleError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * User: Bjorn Harvold
 * Date: Sep 7, 2009
 * Time: 9:38:16 PM
 * Responsibility: Manages views for account
 */
@Controller
@SessionAttributes(types = Relationship.class)
public class FriendsController {

    /** Field description */
    private final static Logger log = LoggerFactory.getLogger(FriendsController.class);

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final AdministrationService administrationService;

    private final RelationshipService relationshipService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param administrationService administrationService
     * @param relationshipService
     */
    @Autowired
    public FriendsController(AdministrationService administrationService, RelationshipService relationshipService) {
        this.administrationService = administrationService;
        this.relationshipService = relationshipService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/friends", method = RequestMethod.GET)
    public String friendDashboard(Model map) throws Exception {
        ApplicationUser user   = SpringSecurityHelper.getSecurityContextPrincipal();
        String     userId = user.getId();

        map.addAttribute("players", administrationService.findPlayers(userId, null, null, null));

        return "player.friends";
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     * @param name name
     * @param page page
     * @param maxResults maxResults
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/{playerId}/friend/request/list", method = RequestMethod.GET)
    public String friendRequests(@PathVariable("playerId") String playerId, @RequestParam(value = "name",
            required = false) String name, @RequestParam(value = "page", defaultValue = "1",
            required = false) Integer page, @RequestParam(value = "maxResults",
            defaultValue = "10", required = false) Integer maxResults, Model map) throws Exception {
        String       view  = "player.friend.list";
        List<Player> list  = relationshipService.findMyFriendRequests(playerId, name, page - 1, maxResults);
        Long         count = relationshipService.findMyFriendRequestCount(playerId, name);

        map.addAttribute("friendRequests", list);
        map.addAttribute("count", count);
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);

        float nrOfPages = (float) count / maxResults;

        map.addAttribute("maxPages", (int) (((nrOfPages > (int) nrOfPages) || (nrOfPages == 0.0))
                ? nrOfPages + 1
                : nrOfPages));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     * @param name name
     * @param page page
     * @param maxResults maxResults
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/{playerId}/friend/list", method = RequestMethod.GET)
    public String friends(@PathVariable("playerId") String playerId,
                          @RequestParam(value = "name", required = false) String name, @RequestParam(value = "page",
            defaultValue = "1", required = false) Integer page, @RequestParam(value = "maxResults", defaultValue = "10",
            required = false) Integer maxResults, Model map) throws Exception {
        String       view  = "player.friend.list";
        List<Player> list  = relationshipService.findMyFriends(playerId, name, page - 1, maxResults);
        Long         count = relationshipService.findMyFriendsCount(playerId, name);

        map.addAttribute("friends", list);
        map.addAttribute("count", count);
        map.addAttribute("page", page);
        map.addAttribute("maxResults", maxResults);

        float nrOfPages = (float) count / maxResults;

        map.addAttribute("maxPages", (int) (((nrOfPages > (int) nrOfPages) || (nrOfPages == 0.0))
                ? nrOfPages + 1
                : nrOfPages));

        return view;
    }

    /**
     * Method description
     *
     *
     * @param playerId playerId
     * @param requestedPlayerId requestedPlayerId
     * @param map map
     *
     * @return Return value
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "/player/{playerId}/friend/{requestedPlayerId}/request/send", method = RequestMethod.POST)
    public String sendFriendRequest(@PathVariable("playerId") String playerId,
                                    @PathVariable("requestedPlayerId") String requestedPlayerId, Model map)
            throws Exception {
        String view      = "player.friend.request.sent";
        Player requester = administrationService.findPlayer(playerId);
        Player requested = administrationService.findPlayer(requestedPlayerId);

        if ((requester == null) || (requested == null)) {
            List<SimpleError> errors = new ArrayList<SimpleError>();

            errors.add(new SimpleError("error.player.missing"));
            map.addAttribute(WebConstants.ERRORS, errors);
        } else {
            Relationship relationship = new Relationship(requested, requester, RelationshipStatus.AWAITING_RESPONSE);

            relationshipService.persistRelationship(relationship);
        }

        return view;
    }
}
