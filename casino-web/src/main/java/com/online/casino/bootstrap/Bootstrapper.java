/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.bootstrap;

import javax.persistence.EntityManager;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:17:25 AM
 */
public interface Bootstrapper {
    void create() throws BootstrapperException;
}
