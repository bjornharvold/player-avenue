/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.dto.GameFinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
public class GameFinderValidator implements Validator {
	public boolean supports(Class<?> clazz) {
		return GameFinder.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {

		if (!errors.hasErrors()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stakeId", "error.form.field.required", null, "Required field");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "error.form.field.required", null, "Required field");
		}

	}
}