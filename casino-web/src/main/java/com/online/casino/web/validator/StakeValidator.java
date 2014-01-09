/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.Stake;
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
public class StakeValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public StakeValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return Stake.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Stake stake = (Stake) target;

		if (!errors.hasErrors()) {
			if (stake.getId() == null && administrationService.findStakeByHighLow(stake.getCasino().getId(), stake.getHigh(), stake.getLow()) != null) {
				errors.rejectValue("high", "error.stake.exists", null, "Stake pair already exists");
			} else if (stake.getHigh().compareTo(stake.getLow()) == -1 ) {
				errors.rejectValue("high", "error.stake.mismatch", null, "High stake cannot be lower than low stake");
			}
		}

	}
}