
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Linode disk status.
 *
 * @see <a href="https://developers.linode.com/v4/reference/linode#disks">Disks</a>
 */
public enum DiskStatus {
    READY,
    NOT_READY,
    DELETING;

    @JsonCreator
    public static DiskStatus parse(String value) {
        return DiskStatus.valueOf(value.replace(' ', '_').toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().replace('_', ' ').toLowerCase();
    }
}
