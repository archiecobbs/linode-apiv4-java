
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
    private IPv6 slaac;
    private IPv6 linkLocal;
    private IPv6Pool[] global;

    public IPv6[] getAddresses() {
        return this.addresses;
    }
    public void setAddresses(final IPv6[] addresses) {
        this.addresses = addresses;
    }

    public IPv6 getSlaac() {
        return this.slaac;
    }
    public void setSlaac(final IPv6 slaac) {
        this.slaac = slaac;
    }

    @JsonProperty("link_local")
    public IPv6 getLinkLocal() {
        return this.linkLocal;
    }
    public void setLinkLocal(final IPv6 linkLocal) {
        this.linkLocal = linkLocal;
    }

    public IPv6Pool[] getGlobal() {
        return this.global;
    }
    public void setGlobal(final IPv6Pool[] global) {
        this.global = global;
    }
}
