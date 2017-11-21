
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Distribution} list.
 */
public class Distributions extends Paginated<Distribution> {

    public Distributions() {
        super(Distribution.class);
    }
}
