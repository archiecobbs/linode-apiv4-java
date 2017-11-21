
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link IPInfo} list.
 */
public class IPInfos extends Paginated<IPInfo> {

    public IPInfos() {
        super(IPInfo.class);
    }
}
