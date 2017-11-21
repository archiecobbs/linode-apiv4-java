
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Linode volume status.
 *
 * @see <a href="https://developers.linode.com/v4/reference/endpoints/linode/instances/$id/volumes">Volumes</a>
 */
public enum VolumeStatus {
    CREATING,
    ACTIVE,
    RESIZING,
    OFFLINE;

    @JsonCreator
    public static VolumeStatus parse(String value) {
        return VolumeStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
