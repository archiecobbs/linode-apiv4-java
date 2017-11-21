
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

/**
 * Paginated {@link Config} list.
 */
public class Configs extends Paginated<Config> {

    public Configs() {
        super(Config.class);
    }
}
