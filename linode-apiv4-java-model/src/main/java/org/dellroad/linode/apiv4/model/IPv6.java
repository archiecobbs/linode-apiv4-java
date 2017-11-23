
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Linode IPv6 address.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#ips">IPs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPv6 extends IP {
}
