
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Type} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Types extends Paginated<Type> {

    public Types() {
        super(Type.class);
    }
}
