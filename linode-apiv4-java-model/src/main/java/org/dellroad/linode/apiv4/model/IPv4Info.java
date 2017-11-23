
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode IPv4 address info.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#ips">IPs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPv4Info {

    private IPv4[] publicx;
    private IPv4[] privatex;
    private IPv4[] shared;

    public IPv4[] getPublic() {
        return this.publicx;
    }
    public void setPublic(final IPv4[] publicx) {
        this.publicx = publicx;
    }

    public IPv4[] getPrivate() {
        return this.privatex;
    }
    public void setPrivate(final IPv4[] privatex) {
        this.privatex = privatex;
    }

    public IPv4[] getShared() {
        return this.shared;
    }
    public void setShared(final IPv4[] shared) {
        this.shared = shared;
    }
}
