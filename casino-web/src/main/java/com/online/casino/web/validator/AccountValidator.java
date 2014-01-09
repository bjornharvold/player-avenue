/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.validator;

import com.online.casino.domain.entity.Account;
import com.online.casino.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: bjorn Date: Sep 25, 2008 Time: 1:32:16 PM AccountValidator validates
 * that the fields have been filled in and that there are no duplicate accounts.
 * A user can only have one account of each
 */
@Component
public class AccountValidator implements Validator {
    private final AdministrationService administrationService;

    @Autowired
    public AccountValidator(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    public boolean supports(Class<?> clazz) {
		return Account.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Account account = (Account) target;

		if (!errors.hasErrors()) {
			if (administrationService.isDuplicateAccount(account)) {
				errors.rejectValue("currency", "error.account.duplicate");
			}
		}

	}

}