
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link Image} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Images extends Paginated<Image> {

    public Images() {
        super(Image.class);
    }
}
