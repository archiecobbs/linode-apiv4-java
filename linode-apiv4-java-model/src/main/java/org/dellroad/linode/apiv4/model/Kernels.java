
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Kernel} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Kernels extends Paginated<Kernel> {

    public Kernels() {
        super(Kernel.class);
    }
}
