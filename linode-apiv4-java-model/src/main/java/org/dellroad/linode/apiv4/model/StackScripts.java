
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link StackScript} list.
 */
public class StackScripts extends Paginated<StackScript> {

    public StackScripts() {
        super(StackScript.class);
    }
}
