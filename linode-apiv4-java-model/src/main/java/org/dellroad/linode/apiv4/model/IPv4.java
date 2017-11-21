
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linode IPv4 address.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#ips">IPs</a>
 */
public class IPv4 extends IP {

    private String region;
    private int linodeId;

    public String getRegion() {
        return this.region;
    }
    public void setRegion(final String region) {
        this.region = region;
    }

    @JsonProperty("linode_id")
    public int getLinodeId() {
        return this.linodeId;
    }
    public void setLinodeId(final int linodeId) {
        this.linodeId = linodeId;
    }
}
