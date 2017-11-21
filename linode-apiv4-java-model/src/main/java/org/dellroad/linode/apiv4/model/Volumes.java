
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Volume} list.
 */
public class Volumes extends Paginated<Volume> {

    public Volumes() {
        super(Volume.class);
    }
}
