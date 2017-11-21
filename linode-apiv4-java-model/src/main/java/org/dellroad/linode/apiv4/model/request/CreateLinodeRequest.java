
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to create Linode instances.
 *
 * @see org.dellroad.linode.apiv4.spring.LinodeApiRequestSender
 */
public class CreateLinodeRequest extends AbstractDiskRequest {

    private String regionId;
    private String typeId;
    private String group;
    private Integer backupId;
    private Integer imageId;
    private boolean booted;

    @JsonProperty("region")
    public String getRegionId() {
        return this.regionId;
    }
    public void setRegionId(final String regionId) {
        this.regionId = regionId;
    }

    @JsonProperty("type")
    public String getTypeId() {
        return this.typeId;
    }
    public void setTypeId(final String typeId) {
        this.typeId = typeId;
    }

    public String getGroup() {
        return this.group;
    }
    public void setGroup(final String group) {
        this.group = group;
    }

    @JsonProperty("backup_id")
    public Integer getBackupId() {
        return this.backupId;
    }
    public void setBackupId(final Integer backupId) {
        this.backupId = backupId;
    }

    @JsonProperty("image_id")
    public Integer getImageId() {
        return this.imageId;
    }
    public void setImageId(final Integer imageId) {
        this.imageId = imageId;
    }

    public boolean isBooted() {
        return this.booted;
    }
    public void setBooted(final boolean booted) {
        this.booted = booted;
    }
}
