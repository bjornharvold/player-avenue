/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.ApplicationUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM
 */
@Component
public class ForgotPasswordValidator implements Validator {
	public boolean supports(Class<?> clazz) {
		return ApplicationUser.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ApplicationUser user = (ApplicationUser) target;

		if (!errors.hasErrors()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.form.field.required", null, "Required field");

			if (StringUtils.isBlank(user.getKaptcha())) {
				errors.rejectValue("kaptcha", "error.kaptcha.failed", null, "Captcha failed");
			} else if (!StringUtils.equals(user.getKaptcha(), user.getRequiredKaptcha())) {
				errors.rejectValue("kaptcha", "error.kaptcha.mismatch", null, "Kaptcha is wrong");
			}
		}
	}
}