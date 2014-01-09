/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.security;

//~--- non-JDK imports --------------------------------------------------------

import com.online.casino.domain.entity.ApplicationUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/20/10
 * Time: 1:26 AM
 * Responsibility:
 */
public class SpringSecurityHelper {

    /**
     * Retrieves the ApplicationUser from the spring security context. Null if user is not logged in.
     *
     * @return Return value
     */
    public static ApplicationUser getSecurityContextPrincipal() {
        ApplicationUser result = null;
        SecurityContext sc     = SecurityContextHolder.getContext();

        if (sc != null) {
            Authentication authentication = sc.getAuthentication();

            if (authentication != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                if ((principal != null) && (principal instanceof ApplicationUser)) {
                    result = (ApplicationUser) principal;
                }
            }
        }

        return result;
    }
}
