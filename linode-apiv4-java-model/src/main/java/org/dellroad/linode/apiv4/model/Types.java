
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Type} list.
 */
public class Types extends Paginated<Type> {

    public Types() {
        super(Type.class);
    }
}
