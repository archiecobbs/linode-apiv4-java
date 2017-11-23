
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Linode} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Linodes extends Paginated<Linode> {

    public Linodes() {
        super(Linode.class);
    }
}
