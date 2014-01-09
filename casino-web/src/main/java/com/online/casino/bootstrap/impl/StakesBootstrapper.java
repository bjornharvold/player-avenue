/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.domain.entity.Casino;
import com.online.casino.domain.entity.Stake;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required roles into the system
 */
@SuppressWarnings("unchecked")
public class StakesBootstrapper extends AbstractBootstrapper implements Bootstrapper {
	private final static Logger log = LoggerFactory.getLogger(StakesBootstrapper.class);
	private static int populated = 0;
	private static int omitted = 0;
	private final Resource file = new ClassPathResource("bootstrap/stakes.xml");
    private final AdministrationService administrationService;

    public StakesBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Transactional
    @Override
    public void create() throws BootstrapperException {

		if (file.exists()) {
			List<Casino> casinos = administrationService.findAllCasinos();

			for (Casino casino : casinos) {
				Long stakeCount = administrationService.findStakeCount(casino.getId());

				if (stakeCount == 0) {
					processCreation(casino);
				}

				log.info("Populated " + populated + " stakes in db");
				log.info("Omitted " + omitted + " stakes from db. Already exists.");
			}
		}
	}

	private void processCreation(Casino casino) throws BootstrapperException {
		try {

			persist(parseXMLFile(casino));

		} catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
	}

	private List<Stake> parseXMLFile(Casino casino) throws Exception {
		List<Stake> result = new ArrayList<Stake>();

		SAXReader reader = new SAXReader();
		Document document = reader.read(file.getInputStream());
		document.normalize();

		List<Element> roles = document.selectNodes("//stakes/stake");

		Stake stake = null;

		for (Element e : roles) {
			List<Element> cells = e.elements();

			if (cells.size() == 2) {

				String high = cells.get(0).getTextTrim();
				String low = cells.get(1).getTextTrim();

				stake = new Stake();
				stake.setCasino(casino);
				stake.setHigh(new BigDecimal(high));
				stake.setLow(new BigDecimal(low));

				result.add(stake);
			} else {
				log.error("Not the right file. Expecting 2 tokens, found " + cells.size());
			}
		}

		return result;
	}

	/**
	 * Saves the admin users to the db before the application becomes active
	 *
     * @param stakes stakes
     * @throws com.online.casino.bootstrap.BootstrapperException exception
	 *
	 */
	private void persist(List<Stake> stakes) throws BootstrapperException {
		List<Stake> dbList = new ArrayList<Stake>();
		try {

			for (Stake stake : stakes) {
				Stake tmp = administrationService.findStakeByHighLow(stake.getCasino().getId(), stake.getHigh(), stake.getLow());

				if (tmp == null) {
					dbList.add(stake);
					populated++;
				} else {
					log.info("Stake already exists with ratio: " + stake.getHigh() + " / " + stake.getLow());
					omitted++;
				}
			}

			// ready fr save all
			if (dbList.size() > 0) {
				for (Stake stake : dbList) {
					administrationService.persistStake(stake);
				}
			}
		} catch (Exception e) {
			throw new BootstrapperException(e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "StakesBootstrapper";
	}
}