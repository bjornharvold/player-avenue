/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.physhun;

import com.wazeegroup.physhun.framework.ConcreteTransition;
import com.wazeegroup.physhun.framework.Transition;

/**
 * Created by Bjorn Harvold
 * Date: 5/21/11
 * Time: 12:33 AM
 * Responsibility:
 */
public class ConcreteTimeoutTransition extends ConcreteTransition implements Transition {
    private Integer timeoutInSeconds;

    public Integer getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(Integer timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }
}
