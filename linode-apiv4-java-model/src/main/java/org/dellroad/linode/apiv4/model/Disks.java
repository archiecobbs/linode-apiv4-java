
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Disk} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Disks extends Paginated<Disk> {

    public Disks() {
        super(Disk.class);
    }
}
