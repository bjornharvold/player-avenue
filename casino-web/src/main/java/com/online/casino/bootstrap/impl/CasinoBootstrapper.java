/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.domain.entity.Casino;
import com.online.casino.domain.enums.CasinoStatus;
import com.online.casino.domain.enums.Currency;
import com.online.casino.service.AdministrationService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required roles into the system
 */
@SuppressWarnings("unchecked")
public class CasinoBootstrapper extends AbstractBootstrapper implements Bootstrapper {
	private final static Logger log = LoggerFactory.getLogger(CasinoBootstrapper.class);
	private static int populated = 0;
	private static int omitted = 0;
	private final Resource file = new ClassPathResource("bootstrap/casinos.xml");
    private final AdministrationService administrationService;

    public CasinoBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Transactional
    @Override
    public void create() throws BootstrapperException {

		if (file.exists()) {
			processCreation();
		} else {
			throw new BootstrapperException("CSV file could not be found");
		}

		log.info("Populated " + populated + " casinos in db");
		log.info("Omitted " + omitted + " casinos from db. Already exists.");
	}

	private void processCreation() throws BootstrapperException {
		try {

			persist(parseXMLFile());

		} catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
	}

	private List<Casino> parseXMLFile() throws Exception {
		List<Casino> result = new ArrayList<Casino>();

		SAXReader reader = new SAXReader();
		Document document = reader.read(file.getInputStream());
		document.normalize();

		List<Element> casinos = document.selectNodes("//casinos/casino");

		Casino casino = null;

		for (Element e : casinos) {

			String name = e.valueOf("@name");

			casino = new Casino();
			casino.setName(name);
			casino.setStatus(CasinoStatus.ACTIVE);

            String id = e.valueOf("@id");
			String currencyS = e.selectSingleNode("//casino[@name='" + name + "']/currency").getStringValue();
			String gameBuffer = e.selectSingleNode("//casino[@name='" + name + "']/game_buffer").getStringValue();
			String emptyGameMinimumLimit = e.selectSingleNode("//casino[@name='" + name + "']/empty_game_minimum_limit").getStringValue();
			String emptyGameMaximumLimit = e.selectSingleNode("//casino[@name='" + name + "']/empty_game_maximum_limit").getStringValue();
			String deleteGamesOnCheck = e.selectSingleNode("//casino[@name='" + name + "']/delete_games_on_check").getStringValue();

            casino.setId(id);
			casino.setGameBuffer(Integer.parseInt(gameBuffer));
			casino.setEmptyGameMinimumLimit(Integer.parseInt(emptyGameMinimumLimit));
			casino.setEmptyGameMaximumLimit(Integer.parseInt(emptyGameMaximumLimit));
			casino.setDeleteGamesOnCheck(Integer.parseInt(deleteGamesOnCheck));
			casino.setCurrency(Currency.valueOf(currencyS));

			result.add(casino);

		}

		return result;
	}

	/**
	 * Saves the admin users to the db before the application becomes active
	 *
     * @param casinos casinos
     * @throws com.online.casino.bootstrap.BootstrapperException exception
	 *
	 */
	private void persist(List<Casino> casinos) throws BootstrapperException {
		List<Casino> dbList = new ArrayList<Casino>();
		try {

			for (Casino casino : casinos) {
				Casino tmp = null;
				try {
					tmp = administrationService.findCasinoByName(casino.getName());
				} catch (EmptyResultDataAccessException e) {
					log.info("Casino with name: " + casino.getName() + " does not exist.");
				}

				if (tmp == null) {
					dbList.add(casino);
					populated++;
				} else {
					log.info("Casino already exists with name: " + casino.getName());
					omitted++;
				}
			}

			// ready for save all
			if (dbList.size() > 0) {
				for (Casino casino : dbList) {
					administrationService.persistCasino(casino);
				}
			}

		} catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "CasinoBootstrapper";
	}
}
