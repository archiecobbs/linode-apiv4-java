
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Image} list.
 */
public class Images extends Paginated<Image> {

    public Images() {
        super(Image.class);
    }
}
