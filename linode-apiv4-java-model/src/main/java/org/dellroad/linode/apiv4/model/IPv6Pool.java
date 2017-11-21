
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * {@link IPv6} pool.
 */
public class IPv6Pool {

    private String range;
    private String region;

    public String getRange() {
        return this.range;
    }
    public void setRange(final String range) {
        this.range = range;
    }

    public String getRegion() {
        return this.region;
    }
    public void setRegion(final String region) {
        this.region = region;
    }
}
