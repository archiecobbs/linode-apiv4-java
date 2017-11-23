
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Volume} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volumes extends Paginated<Volume> {

    public Volumes() {
        super(Volume.class);
    }
}
