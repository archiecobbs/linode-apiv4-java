
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Linode IPv6 address info.
 *
 * @see <a href="https://developers.linode.com/v6/reference/linode#ips">IPs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPv6Info {

    private IPv6[] addresses;
    private IPv6 slacc;
    private String linkLocal;
    private IPv6Pool[] global;

    public IPv6[] getAddresses() {
        return this.addresses;
    }
    public void setAddresses(final IPv6[] addresses) {
        this.addresses = addresses;
    }

    public IPv6 getSlacc() {
        return this.slacc;
    }
    public void setSlacc(final IPv6 slacc) {
        this.slacc = slacc;
    }

    @JsonProperty("link_local")
    public String getLinkLocal() {
        return this.linkLocal;
    }
    public void setLinkLocal(final String linkLocal) {
        this.linkLocal = linkLocal;
    }

    public IPv6Pool[] getGlobal() {
        return this.global;
    }
    public void setGlobal(final IPv6Pool[] global) {
        this.global = global;
    }
}
