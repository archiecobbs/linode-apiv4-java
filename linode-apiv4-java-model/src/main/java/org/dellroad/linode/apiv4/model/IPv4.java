
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linode IPv4 address.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#ips">IPs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPv4 extends IP {

    private String regionId;
    private int linodeId;

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }

    @JsonProperty("linode_id")
    public int getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final int linodeId) {
        this.linodeId = linodeId;
    }
}
