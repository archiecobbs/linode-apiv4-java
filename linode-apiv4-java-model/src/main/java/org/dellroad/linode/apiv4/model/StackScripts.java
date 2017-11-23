
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Paginated {@link StackScript} list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackScripts extends Paginated<StackScript> {

    public StackScripts() {
        super(StackScript.class);
    }
}
