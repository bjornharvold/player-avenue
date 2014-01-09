/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.Player;
import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
@Component
public class PlayerValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public PlayerValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return Player.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Player player = (Player) target;

		if (!errors.hasErrors()) {
			// check for username availability
			if (player.getId() == null && StringUtils.isNotBlank(player.getNickname()) &&
                    administrationService.findPlayerByNickname(player.getNickname()) != null) {
				errors.rejectValue("nickname", "error.nickname.taken", null, "Nickname is already taken");
			}
		}
	}
}