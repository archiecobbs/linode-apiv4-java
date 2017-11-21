
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@link Config} device.
 */
public class Device {

    private int diskId;
    private int volumeId;

    @JsonProperty("disk_id")
    public int getDiskId() {
        return this.diskId;
    }
    public void setDiskId(final int diskId) {
        this.diskId = diskId;
    }

    @JsonProperty("volume_id")
    public int getVolumeId() {
        return this.volumeId;
    }
    public void setVolumeId(final int volumeId) {
        this.volumeId = volumeId;
    }
}
