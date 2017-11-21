
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * {@link Disk} or {@link Image} filesystem.
 */
public enum Filesystem {
    RAW,
    SWAP,
    EXT3,
    EXT4,
    INITRD;

    @JsonCreator
    public static Filesystem parse(String value) {
        return Filesystem.valueOf(value.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
