
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Kernel} list.
 */
public class Kernels extends Paginated<Kernel> {

    public Kernels() {
        super(Kernel.class);
    }
}
