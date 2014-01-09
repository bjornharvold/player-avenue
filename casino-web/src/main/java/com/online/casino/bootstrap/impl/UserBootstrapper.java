/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.UserException;
import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.domain.enums.*;
import com.online.casino.domain.entity.*;

import com.online.casino.service.AdministrationService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * It expects a regular sql file to parse and create country objects for.
 */
@SuppressWarnings("unchecked")
public class UserBootstrapper extends AbstractBootstrapper implements Bootstrapper {
	private final static Logger log = LoggerFactory.getLogger(UserBootstrapper.class);
	private static int populated = 0;
	private static int omitted = 0;
	private final Resource file = new ClassPathResource("bootstrap/users.xml");
    private final AdministrationService administrationService;

	public UserBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Transactional
    @Override
	public void create() throws BootstrapperException {

		if (file.exists()) {
			processCreation();
		} else {
			throw new BootstrapperException("XML file: users.xml could not be found");
		}

		log.info("Populated " + populated + " users in db");
		log.info("Omitted " + omitted + " user from db. Already exists.");
	}

	private void processCreation() throws BootstrapperException {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file.getInputStream());
			document.normalize();

			List<Element> users = document.selectNodes("//users/user");

			ApplicationUser user = null;

			for (Element e : users) {
				ApplicationUser tmp = null;
				String username = e.valueOf("@username");

				// now we save the user
				tmp = administrationService.findApplicationUserByUsername(username);

				if (tmp == null) {
					String id = e.valueOf("@id");
					String password = e.selectSingleNode("//user[@username='" + username + "']/password").getStringValue();
					// String firstName = e.selectSingleNode("//user[@username='" + username + "']/firstName").getStringValue();
					String locale = e.selectSingleNode("//user[@username='" + username + "']/locale").getStringValue();
					String email = e.selectSingleNode("//user[@username='" + username + "']/email").getStringValue();

					user = new ApplicationUser();
                    user.setId(id);
					user.setUsername(username);
					user.setEmail(email);
					user.setStatus(UserStatus.ACTIVE);
                    user.setLocale(locale);
                    
                    // trying to bypass the new bean validator from Hibernate 3.5.x+ by filling in all fields
                    // for automatic validation
                    user.setCurrentPassword(user.getPassword());
                    user.setKaptcha("kaptcha");
                    user.setKaptchaEnabled(Boolean.FALSE);
                    user.setNewPassword(password);

					// save
					administrationService.persistApplicationUser(user);

					populated++;

					processRoles(user, e);

					processPlayers(user, e);

					processAccounts(user, e);

				} else {
					log.info("Admin user already exists: " + tmp.getUsername());
					omitted++;
				}
			}
		} catch (ConstraintViolationException e) {
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                log.error(violation.toString());
            }
            throw new BootstrapperException(e.getMessage(), e);
        } catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
	}

	private void processRoles(ApplicationUser user, Element e) throws UserException {
		// then we add user roles
		List<Element> roles = e.selectNodes("//user[@username='" + user.getUsername() + "']/roles/role");
		for (Element role : roles) {

			ApplicationRole r = administrationService.findSystemRoleByName(role.getText());

			if (r != null) {
				ApplicationUserRole ur = new ApplicationUserRole(user, r);
				administrationService.persistApplicationUserRole(ur);
			} else {
				log.error("Could not find ApplicationRole with name: " + role.getText());
			}       
		}
	}

	private void processPlayers(ApplicationUser user, Element e) throws UserException {
		// then we add user players
		List<Element> players = e.selectNodes("//user[@username='" + user.getUsername() + "']/players/player");
		for (Element element : players) {
			String nickname = element.element("nickname").getStringValue();
			String avatar = element.element("avatar").getStringValue();
			Player p = new Player();
			p.setNickname(nickname);
			p.setAvatarUrl(avatar);
			p.setStatus(PlayerStatus.ACTIVE);
			p.setApplicationUser(user);

			administrationService.persistPlayer(p);
		}
	}

	private void processAccounts(ApplicationUser user, Element e) throws UserException {
		// then we add accounts
		List<Element> accounts = e.selectNodes("//user[@username='" + user.getUsername() + "']/accounts/account");
		for (Element element : accounts) {
			String currencyS = element.attributeValue("currency");
			Currency currency = Currency.valueOf(currencyS);

			Account account = new Account();
			account.setCurrency(currency);
			account.setStatus(AccountStatus.ACTIVE);

			account.setApplicationUser(user);

			administrationService.persistAccount(account);

			// now we need to do an account transfer in order to put money on the user's account
			String amountS = element.attributeValue("amount");
			BigDecimal amount = new BigDecimal(amountS);
			AccountTransfer af = new AccountTransfer();
            af.setAccountId(account.getId());
			af.setStatus(AccountTransferStatus.COMPLETE);
			af.setAmount(amount);
			af.setType(AccountTransferType.BANK_TRANSFER);
			af.setAction(AccountTransferAction.DEPOSIT);
			administrationService.persistAccountTransfer(af);
			
			AccountEntry entry = new AccountEntry(account, amount, "Deposit from type: " + af.getType().name(), AccountEntryType.DEPOSIT);
			administrationService.persistAccountEntry(entry);
			
		}
	}

	@Override
	public String toString() {
		return "UserBootstrapper";
	}
}
