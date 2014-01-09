/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap.impl;

import com.online.casino.bootstrap.Bootstrapper;
import com.online.casino.bootstrap.BootstrapperException;
import com.online.casino.domain.entity.ApplicationRole;
import com.online.casino.domain.enums.SystemRightType;
import com.online.casino.service.AdministrationService;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required roles into the system
 */
@SuppressWarnings("unchecked")
public class SystemRoleBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(SystemRoleBootstrapper.class);
    private static int populated = 0;
    private static int omitted = 0;
    private final Resource file = new ClassPathResource("bootstrap/system_roles.xml");
    private final AdministrationService administrationService;

    public SystemRoleBootstrapper(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Transactional
    @Override
    public void create() throws BootstrapperException {

        if (file.exists()) {
            processCreation();
        } else {
            throw new BootstrapperException("XML file could not be found");
        }

        log.info("Populated " + populated + " roles in db");
        log.info("Omitted " + omitted + " roles from db. Already exists.");
    }

    private void processCreation() throws BootstrapperException {
        try {

            persist(parseXMLFile());

        } catch (Exception e) {
            throw new BootstrapperException(e.getMessage(), e);
        }
    }

    private List<ApplicationRole> parseXMLFile() throws Exception {
        List<ApplicationRole> result = new ArrayList<ApplicationRole>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(file.getInputStream());
        document.normalize();

        List<Element> roles = document.selectNodes("//roles/role");

        ApplicationRole role = null;

        for (Element e : roles) {

            String name = e.elementText("name");
            String description = e.elementText("description");
            String statusCode = e.elementText("statusCode");

            role = new ApplicationRole();
            role.setName(name);
            role.setDescription(description);

            Element rightsElement = e.element("rights");

            if (rightsElement != null) {

                List<Element> rightElementList = rightsElement.elements("right");

                if (rightElementList != null && !rightElementList.isEmpty()) {
                    for (Element element : rightElementList) {
                        String rightStatusCode = element.attributeValue("statusCode");

                        if (StringUtils.isNotBlank(rightStatusCode)) {
                            role.getRights().add(administrationService.findSystemRightByStatusCode(SystemRightType.valueOf(rightStatusCode)));
                        }
                    }
                }
            }

            result.add(role);

        }

        return result;
    }

    /**
     * Saves the admin users to the db before the application becomes active
     *
     * @param roles roles
     * @throws com.online.casino.bootstrap.BootstrapperException
     *
     */
    private void persist(List<ApplicationRole> roles) throws BootstrapperException {
        List<ApplicationRole> dbList = new ArrayList<ApplicationRole>();

        try {

            for (ApplicationRole role : roles) {
                ApplicationRole tmp = administrationService.findSystemRoleByName(role.getName());

                if (tmp == null) {
                    dbList.add(role);
                    populated++;
                } else {
                    log.info("ApplicationRole already exists with status code: " + role.getName());
                    omitted++;
                }
            }

            // ready for save
            if (dbList.size() > 0) {
                for (ApplicationRole role : dbList) {
                    administrationService.persistSystemRole(role);
                }
            }
        } catch (Exception e) {
            throw new BootstrapperException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "SystemRoleBootstrapper";
    }
}