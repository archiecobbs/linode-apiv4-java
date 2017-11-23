
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Distribution} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Distributions extends Paginated<Distribution> {

    public Distributions() {
        super(Distribution.class);
    }
}
