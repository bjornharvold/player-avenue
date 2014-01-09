/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.GameTemplate;
import com.online.casino.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: bjorn
 * Date: Sep 25, 2008
 * Time: 1:32:16 PM
 */
@Component
public class GameTemplateValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public GameTemplateValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return GameTemplate.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		GameTemplate template = (GameTemplate) target;

		if (!errors.hasErrors()) {
			// check for duplicates
			if (template.getId() == null && template.getCasino() != null &&
                    administrationService.findGameTemplateByValues(template.getCasino().getId(), template.getDeviceType(), template.getLimitType(), template.getMaxPlayers(), template.getStake().getId(), template.getType()) != null) {
				errors.rejectValue("currency", "error.poker.template.taken", null, "Poker template already exists");
			}
		}

	}
}