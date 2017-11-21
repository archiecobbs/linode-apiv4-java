
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Disk} list.
 */
public class Disks extends Paginated<Disk> {

    public Disks() {
        super(Disk.class);
    }
}
