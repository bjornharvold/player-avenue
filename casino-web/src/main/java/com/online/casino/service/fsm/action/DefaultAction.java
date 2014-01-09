/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */


package com.online.casino.service.fsm.action;

//~--- non-JDK imports --------------------------------------------------------

import com.wazeegroup.physhun.framework.AbstractAction;
import com.wazeegroup.physhun.framework.ProcessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 * @author Bjorn Harvold
 * @version ${project.version}, 10/12/18
 */
@Component
public class DefaultAction extends AbstractAction {

    /**
     * Field description
     */
    private final static Logger log = LoggerFactory.getLogger(DefaultAction.class);

    //~--- fields -------------------------------------------------------------

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param po po
     */
    @Override
    public void execute(ProcessObject po) {
        if (log.isDebugEnabled()) {
            log.debug("Executing DefaultAction...");
        }
    }
}
