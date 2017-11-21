
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Linode} list.
 */
public class Linodes extends Paginated<Linode> {

    public Linodes() {
        super(Linode.class);
    }
}
