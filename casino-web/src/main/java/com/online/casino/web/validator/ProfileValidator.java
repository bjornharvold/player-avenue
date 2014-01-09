/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.ApplicationUser;
import com.online.casino.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
@Component
public class ProfileValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public ProfileValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return ApplicationUser.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ApplicationUser user = (ApplicationUser) target;

		if (!errors.hasErrors()) {
			// check for username availability
			if (user.getId() == null && administrationService.findApplicationUserByUsername(user.getUsername()) != null) {
				errors.rejectValue("username", "error.username.taken", null, "Username is already taken");
			} else if (user.getId() != null && administrationService.isApplicationUserUniqueByUsername(user.getId(), user.getUsername()) != null) {
				errors.rejectValue("username", "error.username.taken", null, "Username is already taken");
			}

			// check for email availability
			if (user.getId() == null && administrationService.findApplicationUserByEmail(user.getEmail()) != null) {
				errors.rejectValue("email", "error.email.taken", null, "E-mail is already taken");
			} else if (user.getId() != null && administrationService.isApplicationUserUniqueByEmail(user.getId(), user.getEmail()) != null) {
				errors.rejectValue("email", "error.email.taken", null, "E-mail is already taken");
			}
		}
	}
}