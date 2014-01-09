/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.web.utils;

/**
 * User: Bjorn Harvold
 * Date: Sep 10, 2009
 * Time: 1:38:24 PM
 * Responsibility:
 */
public class ReferenceData {
    private String label;
    private String value;

    public ReferenceData(String value, String label) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
