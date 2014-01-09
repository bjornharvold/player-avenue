
/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.utils;

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.security.SpringSecurityHelper;
import com.online.casino.service.AdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * User: Bjorn Harvold
 * Date: Sep 17, 2010
 * Time: 3:25:43 PM
 * Responsibility: This is our custom locale resolver for TXLA. Once the user logs 
 * in we will have access to UserProfileData and therefore the locale. Prior to logging in we will default to en_US.
 * The toggle on the webpage can change the locale at runtime. 
 */
public class CustomLocaleResolver extends CookieLocaleResolver implements LocaleResolver {
    private final static Logger log = LoggerFactory.getLogger(CustomLocaleResolver.class);
    private final AdministrationService administrationService;

    public CustomLocaleResolver(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale result = null;
        
        ApplicationUser user = SpringSecurityHelper.getSecurityContextPrincipal();
        
        // this is the prioritized locale
        if (user != null && user.getLocale() != null) {
            // TODO might want to optimize later here and put all pre-split locales in memory
            String[] loc = user.getLocale().split("_");
            result = new Locale(loc[0], loc[1]);
        } else {
            result = super.resolveLocale(request);
        }
        
        return result;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        super.setLocale(request, response, locale);
        
        // update the locale in the db
        ApplicationUser user = SpringSecurityHelper.getSecurityContextPrincipal();
        
        if (user != null) {
            // hopefully set the locale on the object in the spring security context
            user.setLocale(locale.toString());
            // grab a fresh copy
            ApplicationUser dbUser = administrationService.findApplicationUser(user.getId());
            // set new locale
            dbUser.setLocale(locale.toString());
            // update
            administrationService.mergeApplicationUser(dbUser);
        }
    }
}
