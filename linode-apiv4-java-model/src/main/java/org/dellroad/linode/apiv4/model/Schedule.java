
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode backup schedule info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {

    private String day;
    private String window;

    public String getDay() {
        return this.day;
    }
    public void setDay(final String day) {
        this.day = day;
    }

    public String getWindow() {
        return this.window;
    }
    public void setWindow(final String window) {
        this.window = window;
    }
}
