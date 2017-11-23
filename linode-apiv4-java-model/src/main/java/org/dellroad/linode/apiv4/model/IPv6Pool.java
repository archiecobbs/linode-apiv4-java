
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link IPv6} pool.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPv6Pool {

    private String range;
    private String regionId;

    public String getRange() {
        return this.range;
    }
    public void setRange(final String range) {
        this.range = range;
    }

    @JsonProperty("regionId")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }
}
