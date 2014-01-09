/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Bjorn Harvold
 * Date: Nov 25, 2009
 * Time: 11:11:38 AM
 * Responsibility:
 */
public class SecureChannelHelper {
    private static final Logger log = LoggerFactory.getLogger(SecureChannelHelper.class);
    private static String[] rights = {
            "RIGHT_LOGIN_PLAYER",
            "RIGHT_LOGIN_ADMIN",
            "RIGHT_DEVELOPER",
            "RIGHT_PLAY_GAME",
            "RIGHT_READ_SECURITY",
            "RIGHT_READ_CASINO",
            "RIGHT_READ_CASINO_AS_ADMIN",
            "RIGHT_WRITE_CASINO_AS_ADMIN",
            "RIGHT_UPDATE_CASINO_AS_ADMIN",
            "RIGHT_DELETE_CASINO_AS_ADMIN",
            "RIGHT_READ_ACCOUNT",
            "RIGHT_WRITE_ACCOUNT",
            "RIGHT_UPDATE_ACCOUNT",
            "RIGHT_DELETE_ACCOUNT",
            "RIGHT_READ_ACCOUNT_ENTRY",
            "RIGHT_WRITE_ACCOUNT_ENTRY",
            "RIGHT_UPDATE_ACCOUNT_ENTRY",
            "RIGHT_DELETE_ACCOUNT_ENTRY",
            "RIGHT_READ_ACCOUNT_TRANSFER",
            "RIGHT_WRITE_ACCOUNT_TRANSFER",
            "RIGHT_UPDATE_ACCOUNT_TRANSFER",
            "RIGHT_DELETE_ACCOUNT_TRANSFER",
            "RIGHT_READ_BET",
            "RIGHT_WRITE_BET",
            "RIGHT_UPDATE_BET",
            "RIGHT_DELETE_BET",
            "RIGHT_READ_GAMBLER",
            "RIGHT_WRITE_GAMBLER",
            "RIGHT_UPDATE_GAMBLER",
            "RIGHT_DELETE_GAMBLER",
            "RIGHT_READ_GAMBLER_ACCOUNT_ENTRY",
            "RIGHT_WRITE_GAMBLER_ACCOUNT_ENTRY",
            "RIGHT_UPDATE_GAMBLER_ACCOUNT_ENTRY",
            "RIGHT_DELETE_GAMBLER_ACCOUNT_ENTRY",
            "RIGHT_READ_GAME_TEMPLATE_AS_ADMIN",
            "RIGHT_WRITE_GAME_TEMPLATE_AS_ADMIN",
            "RIGHT_UPDATE_GAME_TEMPLATE_AS_ADMIN",
            "RIGHT_DELETE_GAME_TEMPLATE_AS_ADMIN",
            "RIGHT_READ_HAND_AS_ADMIN",
            "RIGHT_WRITE_HAND_AS_ADMIN",
            "RIGHT_UPDATE_HAND_AS_ADMIN",
            "RIGHT_DELETE_HAND_AS_ADMIN",
            "RIGHT_READ_INTERNAL_MESSAGE",
            "RIGHT_WRITE_INTERNAL_MESSAGE",
            "RIGHT_UPDATE_INTERNAL_MESSAGE",
            "RIGHT_DELETE_INTERNAL_MESSAGE",
            "RIGHT_READ_INTERNAL_SYSTEM_MESSAGE",
            "RIGHT_WRITE_INTERNAL_SYSTEM_MESSAGE",
            "RIGHT_UPDATE_INTERNAL_SYSTEM_MESSAGE",
            "RIGHT_DELETE_INTERNAL_SYSTEM_MESSAGE",
            "RIGHT_READ_PLAYER",
            "RIGHT_WRITE_PLAYER",
            "RIGHT_UPDATE_PLAYER",
            "RIGHT_DELETE_PLAYER",
            "RIGHT_READ_POKER_GAME_AS_ADMIN",
            "RIGHT_WRITE_POKER_GAME_AS_ADMIN",
            "RIGHT_UPDATE_POKER_GAME_AS_ADMIN",
            "RIGHT_DELETE_POKER_GAME_AS_ADMIN",
            "RIGHT_READ_QUEUED_GAMBLER_AS_ADMIN",
            "RIGHT_WRITE_QUEUED_GAMBLER_AS_ADMIN",
            "RIGHT_UPDATE_QUEUED_GAMBLER_AS_ADMIN",
            "RIGHT_DELETE_QUEUED_GAMBLER_AS_ADMIN",
            "RIGHT_READ_GAME_OBSERVER_AS_ADMIN",
            "RIGHT_WRITE_GAME_OBSERVER_AS_ADMIN",
            "RIGHT_UPDATE_GAME_OBSERVER_AS_ADMIN",
            "RIGHT_DELETE_GAME_OBSERVER_AS_ADMIN",
            "RIGHT_READ_RELATIONSHIP",
            "RIGHT_WRITE_RELATIONSHIP",
            "RIGHT_UPDATE_RELATIONSHIP",
            "RIGHT_DELETE_RELATIONSHIP",
            "RIGHT_READ_STAKE_AS_ADMIN",
            "RIGHT_WRITE_STAKE_AS_ADMIN",
            "RIGHT_UPDATE_STAKE_AS_ADMIN",
            "RIGHT_DELETE_STAKE_AS_ADMIN",
            "RIGHT_READ_SYSTEM_RIGHT_AS_ADMIN",
            "RIGHT_WRITE_SYSTEM_RIGHT_AS_ADMIN",
            "RIGHT_UPDATE_SYSTEM_RIGHT_AS_ADMIN",
            "RIGHT_DELETE_SYSTEM_RIGHT_AS_ADMIN",
            "RIGHT_READ_SYSTEM_ROLE_AS_ADMIN",
            "RIGHT_WRITE_SYSTEM_ROLE_AS_ADMIN",
            "RIGHT_UPDATE_SYSTEM_ROLE_AS_ADMIN",
            "RIGHT_DELETE_SYSTEM_ROLE_AS_ADMIN",
            "RIGHT_READ_SYSTEM_USER",
            "RIGHT_WRITE_SYSTEM_USER",
            "RIGHT_UPDATE_SYSTEM_USER",
            "RIGHT_DELETE_SYSTEM_USER",
            "RIGHT_READ_SYSTEM_USER_ROLE_AS_ADMIN",
            "RIGHT_WRITE_SYSTEM_USER_ROLE_AS_ADMIN",
            "RIGHT_UPDATE_SYSTEM_USER_ROLE_AS_ADMIN",
            "RIGHT_DELETE_SYSTEM_USER_ROLE_AS_ADMIN",
            "RIGHT_READ_PLAYER_AS_ADMIN",
            "RIGHT_WRITE_PLAYER_AS_ADMIN",
            "RIGHT_UPDATE_PLAYER_AS_ADMIN",
            "RIGHT_DELETE_PLAYER_AS_ADMIN",
            "RIGHT_READ_ACCOUNT_AS_ADMIN",
            "RIGHT_WRITE_ACCOUNT_AS_ADMIN",
            "RIGHT_UPDATE_ACCOUNT_AS_ADMIN",
            "RIGHT_DELETE_ACCOUNT_AS_ADMIN",
            "RIGHT_READ_SYSTEM_USER_AS_ADMIN",
            "RIGHT_WRITE_SYSTEM_USER_AS_ADMIN",
            "RIGHT_UPDATE_SYSTEM_USER_AS_ADMIN",
            "RIGHT_DELETE_SYSTEM_USER_AS_ADMIN",
            "RIGHT_READ_ACCOUNT_TRANSFER_AS_ADMIN",
            "RIGHT_WRITE_ACCOUNT_TRANSFER_AS_ADMIN",
            "RIGHT_UPDATE_ACCOUNT_TRANSFER_AS_ADMIN",
            "RIGHT_DELETE_ACCOUNT_TRANSFER_AS_ADMIN",
            "RIGHT_UPDATE_PASSWORD"
    };

    public static void secureChannel() {
        log.info("Securing channel...");
//        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

            for (String right : rights) {
                auths.add(new SimpleGrantedAuthority(right));
            }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("test", "test", auths);
            SecurityContextHolder.getContext().setAuthentication(token);

            log.info("Channel administration");
//        } else {
//            log.info("Channel already secured");
//        }
    }

    public static void unsecureChannel() {
        log.info("Un-securing channel...");
        SecurityContextHolder.getContext().setAuthentication(null);
        log.info("Channel insecure");
    }
}
