
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link IPInfo} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPInfos extends Paginated<IPInfo> {

    public IPInfos() {
        super(IPInfo.class);
    }
}
