
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Region} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Regions extends Paginated<Region> {

    public Regions() {
        super(Region.class);
    }
}
