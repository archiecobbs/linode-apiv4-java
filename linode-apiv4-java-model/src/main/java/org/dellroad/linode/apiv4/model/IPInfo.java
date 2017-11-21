
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Linode IP address info.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#ips">IPs</a>
 */
public class IPInfo {

    private IPv4Info ipv4;
    private IPv6Info ipv6;

    public IPv4Info getIPv4() {
        return this.ipv4;
    }
    public void setIPv4(final IPv4Info ipv4) {
        this.ipv4 = ipv4;
    }

    public IPv6Info getIPv6() {
        return this.ipv6;
    }
    public void setIPv6(final IPv6Info ipv6) {
        this.ipv6 = ipv6;
    }
}
