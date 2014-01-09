/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.security;

import com.online.casino.domain.entity.Account;
import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.domain.entity.Player;
import com.online.casino.domain.enums.SystemRightType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * Created by Bjorn Harvold
 * Date: 3/17/11
 * Time: 3:42 PM
 * Responsibility:
 */
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final static Logger log = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    private static enum permissionType {delete, list, merge, persist}

    @Override
    public boolean hasPermission(Authentication auth, Object target, Object perm) {

        if (target instanceof Account) {
            Account account = (Account) target;
            return processAccountPermission(auth, account, perm);
        }

        if (target instanceof Player) {
            Player player = (Player) target;
            return processPlayerPermission(auth, player, perm);
        }

        if (target instanceof ApplicationUser) {
            ApplicationUser applicationUser = (ApplicationUser) target;
            return processApplicationUserPermission(auth, applicationUser, perm);
        }
        
        throw new UnsupportedOperationException("hasPermission not supported for object <" + target + "> and permission <" + perm + ">");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Only an admin or the user who created the player can player actions
     *
     * @param auth   authentication
     * @param player player
     * @param perm
     * @return
     */
    private boolean processPlayerPermission(Authentication auth, Player player, Object perm) {
        boolean result = false;

        permissionType t = permissionType.valueOf((String) perm);

        ApplicationUser principal = (ApplicationUser) auth.getPrincipal();
        
        switch (t) {
            case delete:
                result = StringUtils.equals(player.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_DELETE_PLAYER.name());
                break;

            case list:
                result = StringUtils.equals(player.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_READ_PLAYER.name()) ||
                        hasRole(auth, SystemRightType.RIGHT_READ_PLAYER_AS_ADMIN.name());
                break;
            
            case merge:
                result = StringUtils.equals(player.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_UPDATE_PLAYER.name());
                break;
            
            case persist:
                result = StringUtils.equals(player.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_WRITE_PLAYER.name());
                break;
        }

        return result;
    }
    
    private boolean processAccountPermission(Authentication auth, Account account, Object perm) {
        boolean result = false;

        permissionType t = permissionType.valueOf((String) perm);

        ApplicationUser principal = (ApplicationUser) auth.getPrincipal();
        
        switch (t) {
            case delete:
                result = StringUtils.equals(account.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_DELETE_ACCOUNT.name());
                break;

            case list:
                result = StringUtils.equals(account.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_READ_ACCOUNT.name()) ||
                        hasRole(auth, SystemRightType.RIGHT_READ_ACCOUNT_AS_ADMIN.name());
                break;
            
            case merge:
                result = StringUtils.equals(account.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_UPDATE_ACCOUNT.name());
                break;
            
            case persist:
                result = StringUtils.equals(account.getApplicationUser().getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_WRITE_ACCOUNT.name());
                break;
        }

        return result;
    }

    private boolean processApplicationUserPermission(Authentication auth, ApplicationUser applicationUser, Object perm) {
        boolean result = false;

        permissionType t = permissionType.valueOf((String) perm);

        ApplicationUser principal = (ApplicationUser) auth.getPrincipal();

        switch (t) {

            case merge:
                result = StringUtils.equals(applicationUser.getId(), principal.getId()) &&
                        hasRole(auth, SystemRightType.RIGHT_UPDATE_SYSTEM_USER.name());
                break;
        }

        return result;
    }

    private boolean hasRole(Authentication auth, String role) {
        for (GrantedAuthority right : auth.getAuthorities()) {
            if (right.getAuthority().equals(role)) {
                return true;
            }
        }

        return false;
    }
}
