
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Region} list.
 */
public class Regions extends Paginated<Region> {

    public Regions() {
        super(Region.class);
    }
}
