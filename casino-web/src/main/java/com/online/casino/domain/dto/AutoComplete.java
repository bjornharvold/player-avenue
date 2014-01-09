/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold. 
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.online.casino.domain.dto;

/**
 * User: Bjorn Harvold
 * Date: Nov 2, 2010
 * Time: 10:23:21 PM
 * Responsibility:
 */
public class AutoComplete {
    private String id;
    private String label;
    private String value;

    public AutoComplete(String id, String label) {
        this.id = id;
        this.label = label;
        this.value = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
